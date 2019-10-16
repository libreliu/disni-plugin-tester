/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.ustc.acsa.test;

import com.ibm.disni.RdmaActiveEndpoint;
import com.ibm.disni.RdmaActiveEndpointGroup;
import com.ibm.disni.RdmaEndpointFactory;
import com.ibm.disni.verbs.IbvMr;
import com.ibm.disni.verbs.IbvRecvWR;
import com.ibm.disni.verbs.IbvSendWR;
import com.ibm.disni.verbs.IbvSge;
import com.ibm.disni.verbs.IbvWC;
import com.ibm.disni.verbs.RdmaCmId;
import com.ibm.disni.verbs.SVCPostSend;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.commons.cli.ParseException;



public class FakeClient implements RdmaEndpointFactory<FakeClient.CustomClientEndpoint> {
	private RdmaActiveEndpointGroup<FakeClient.CustomClientEndpoint> endpointGroup;
	private String host;
	private int port;

	public FakeClient.CustomClientEndpoint createEndpoint(RdmaCmId idPriv, boolean serverSide) throws IOException {
		return new FakeClient.CustomClientEndpoint(endpointGroup, idPriv, serverSide);
	}

	public void run() throws Exception {
		//create a EndpointGroup. The RdmaActiveEndpointGroup contains CQ processing and delivers CQ event to the endpoint.dispatchCqEvent() method.
		endpointGroup = new RdmaActiveEndpointGroup<FakeClient.CustomClientEndpoint>(1000, false, 128, 4, 128);
		endpointGroup.init(this);
		//we have passed our own endpoint factory to the group, therefore new endpoints will be of type CustomClientEndpoint
		//let's create a new client endpoint
		FakeClient.CustomClientEndpoint endpoint = endpointGroup.createEndpoint();

		//connect to the server
 		InetAddress ipAddress = InetAddress.getByName(host);
 		InetSocketAddress address = new InetSocketAddress(ipAddress, port);			
		endpoint.connect(address, 1000);
		InetSocketAddress _addr = (InetSocketAddress) endpoint.getDstAddr();
		System.out.println("FakeClient::client connected, address " + _addr.toString());

                //let's prepare a message to be sent to the client
		//in the message we include the RDMA information of a local buffer which we allow the client to read using a one-sided RDMA operation
		
		ByteBuffer sendBuf = endpoint.getSendBuf();
                		
		sendBuf.asCharBuffer().put("Hello from DiSNI!!!CAN U HEAR ME???\n");
		sendBuf.clear();

		//post the operation to send the message
		System.out.println("ReadServer::sending message 1");
		endpoint.postSend(endpoint.getWrList_send()).execute().free();
                
                ///////////////////////////////////////////
                ByteBuffer sendBuf2 = endpoint.getSendBuf2();
                		
		sendBuf2.asCharBuffer().put("Hello number two!!\n");
		sendBuf2.clear();

		//post the operation to send the message
		System.out.println("ReadServer::sending message 2");
		endpoint.postSend(endpoint.getWrList_send2()).execute().free();
                Thread.sleep(1000);
//                
//		//in our custom endpoints we make sure CQ events get stored in a queue, we now query that queue for new CQ events.
//		//in this case a new CQ event means we have received some data, i.e., a message from the server
//		endpoint.getWcEvents().take();
//		ByteBuffer recvBuf = endpoint.getRecvBuf();
//		//the message has been received in this buffer
//		//it contains some RDMA information sent by the server
//		recvBuf.clear();
//		long addr = recvBuf.getLong();
//		int length = recvBuf.getInt();
//		int lkey = recvBuf.getInt();
//		recvBuf.clear();
//		System.out.println("FakeClient::receiving rdma information, addr " + addr + ", length " + length + ", key " + lkey);
//		System.out.println("FakeClient::preparing read operation...");
//
//		//the RDMA information above identifies a RDMA buffer at the server side
//		//let's issue a one-sided RDMA read opeation to fetch the content from that buffer
//		IbvSendWR sendWR = endpoint.getSendWR();
//		sendWR.setWr_id(1001);
//		sendWR.setOpcode(IbvSendWR.IBV_WR_RDMA_READ);
//		sendWR.setSend_flags(IbvSendWR.IBV_SEND_SIGNALED);
//		sendWR.getRdma().setRemote_addr(addr);
//		sendWR.getRdma().setRkey(lkey);
//
//		//post the operation on the endpoint
//		SVCPostSend postSend = endpoint.postSend(endpoint.getWrList_send());
//		for (int i = 10; i <= 100; ){
//			postSend.getWrMod(0).getSgeMod(0).setLength(i);
//			postSend.execute();
//			//wait until the operation has completed
//			endpoint.getWcEvents().take();
//
//			//we should have the content of the remote buffer in our own local buffer now
//			ByteBuffer dataBuf = endpoint.getDataBuf();
//			dataBuf.clear();
//			System.out.println("FakeClient::read memory from server: " + dataBuf.asCharBuffer().toString());
//			i += 10;
//		}
//
//		//let's prepare a final message to signal everything went fine
//		sendWR.setWr_id(1002);
//		sendWR.setOpcode(IbvSendWR.IBV_WR_SEND);
//		sendWR.setSend_flags(IbvSendWR.IBV_SEND_SIGNALED);
//		sendWR.getRdma().setRemote_addr(addr);
//		sendWR.getRdma().setRkey(lkey);
//
//		//post that operation
//		endpoint.postSend(endpoint.getWrList_send()).execute().free();

		//close everything
		System.out.println("closing endpoint");
		endpoint.close();
		System.out.println("closing endpoint, done");
		endpointGroup.close();
	}

	public void launch(String host, String port) throws Exception {

		this.host = host;
		this.port = Integer.parseInt(port);

		this.run();
	}

	public static void main(String[] args) throws Exception {
                if (args.length != 2) {
                    System.out.println("Usage: program host port");
                    return;
                }
                
                FakeClient simpleClient = new FakeClient();
		simpleClient.launch(args[0], args[1]);
	}

	public static class CustomClientEndpoint extends RdmaActiveEndpoint {
		private ByteBuffer buffers[];
		private IbvMr mrlist[];
		private int buffercount = 3;
		private int buffersize = 100;

		private ByteBuffer dataBuf;
		private IbvMr dataMr;
		private ByteBuffer sendBuf;
                private ByteBuffer sendBuf2;
		private ByteBuffer recvBuf;
		private IbvMr recvMr;

                private IbvMr sendMr;
                private IbvMr sendMr2;
                
		private LinkedList<IbvSendWR> wrList_send;
                private LinkedList<IbvSendWR> wrList_send2;
                
		private IbvSge sgeSend;
		private LinkedList<IbvSge> sgeList;
		private IbvSendWR sendWR;

		private LinkedList<IbvRecvWR> wrList_recv;
		private IbvSge sgeRecv;
		private LinkedList<IbvSge> sgeListRecv;
		private IbvRecvWR recvWR;

		private ArrayBlockingQueue<IbvWC> wcEvents;

		public CustomClientEndpoint(RdmaActiveEndpointGroup<? extends CustomClientEndpoint> endpointGroup, RdmaCmId idPriv, boolean isServerSide) throws IOException {
			super(endpointGroup, idPriv, isServerSide);
			this.buffercount = 4;
			this.buffersize = 100;
			buffers = new ByteBuffer[buffercount];
			this.mrlist = new IbvMr[buffercount];

			for (int i = 0; i < buffercount; i++){
				buffers[i] = ByteBuffer.allocateDirect(buffersize);
			}

			this.wrList_send = new LinkedList<IbvSendWR>();
                        this.wrList_send2 = new LinkedList<IbvSendWR>();
                        
			this.sgeSend = new IbvSge();
			this.sgeList = new LinkedList<IbvSge>();
			this.sendWR = new IbvSendWR();

			this.wrList_recv = new LinkedList<IbvRecvWR>();
			this.sgeRecv = new IbvSge();
			this.sgeListRecv = new LinkedList<IbvSge>();
			this.recvWR = new IbvRecvWR();

			this.wcEvents = new ArrayBlockingQueue<IbvWC>(10);
		}

		//important: we override the init method to prepare some buffers (memory registration, post recv, etc).
		//This guarantees that at least one recv operation will be posted at the moment this endpoint is connected.
		public void init() throws IOException{
			super.init();

			for (int i = 0; i < buffercount; i++){
				mrlist[i] = registerMemory(buffers[i]).execute().free().getMr();
			}

			this.dataBuf = buffers[0];
			this.dataMr = mrlist[0];
			this.sendBuf = buffers[1];
                        this.sendMr = mrlist[1];
			this.recvBuf = buffers[2];
			this.recvMr = mrlist[2];
                        this.sendBuf2 = buffers[3];
                        this.sendMr2 = mrlist[3];


                        
			dataBuf.clear();
			sendBuf.clear();
                        sendBuf2.clear();
                        
                        
			sgeSend.setAddr(sendMr.getAddr());
			sgeSend.setLength(sendMr.getLength());
			sgeSend.setLkey(sendMr.getLkey());
			sgeList.add(sgeSend);
			sendWR.setWr_id(2000);
			sendWR.setSg_list(sgeList);
			sendWR.setOpcode(IbvSendWR.IBV_WR_SEND);
			sendWR.setSend_flags(IbvSendWR.IBV_SEND_SIGNALED);
			wrList_send.add(sendWR);

                        IbvSendWR sendWR2 = new IbvSendWR();
                        IbvSge sgeSend2 = new IbvSge();
                        LinkedList<IbvSge> sgeList2 = new LinkedList<IbvSge>();
                        sgeSend2.setAddr(sendMr2.getAddr());
			sgeSend2.setLength(sendMr2.getLength());
			sgeSend2.setLkey(sendMr2.getLkey());
			sgeList2.add(sgeSend2);
			sendWR2.setWr_id(2003);
			sendWR2.setSg_list(sgeList2);
			sendWR2.setOpcode(IbvSendWR.IBV_WR_SEND);
			sendWR2.setSend_flags(IbvSendWR.IBV_SEND_SIGNALED);
			wrList_send2.add(sendWR2);


			sgeRecv.setAddr(recvMr.getAddr());
			sgeRecv.setLength(recvMr.getLength());
			int lkey = recvMr.getLkey();
			sgeRecv.setLkey(lkey);
			sgeListRecv.add(sgeRecv);
			recvWR.setSg_list(sgeListRecv);
			recvWR.setWr_id(2001);
			wrList_recv.add(recvWR);

			//System.out.println("FakeClient::initiated recv");
			//this.postRecv(wrList_recv).execute().free();
		}

                public String describeWc(IbvWC wc) {
                    StringBuffer sbuf = new StringBuffer("");
                    
                    sbuf.append("wr_id=" + wc.getWr_id());
                    sbuf.append("status=" + wc.getStatus());
                    sbuf.append("opcode=" + wc.getOpcode());
                    sbuf.append("vendor_err=" + wc.getVendor_err());
                    sbuf.append("byte_len=" + wc.getByte_len());
                    return sbuf.toString();
                }
                
		public void dispatchCqEvent(IbvWC wc) throws IOException {
                        System.out.println("** got wc: " + describeWc(wc));
			wcEvents.add(wc);
		}

		public ArrayBlockingQueue<IbvWC> getWcEvents() {
			return wcEvents;
		}

		public LinkedList<IbvSendWR> getWrList_send() {
			return wrList_send;
		}
                
		public LinkedList<IbvSendWR> getWrList_send2() {
			return wrList_send2;
		}

		public LinkedList<IbvRecvWR> getWrList_recv() {
			return wrList_recv;
		}

		public ByteBuffer getDataBuf() {
			return dataBuf;
		}

		public ByteBuffer getSendBuf() {
			return sendBuf;
		}

                public ByteBuffer getSendBuf2() {
			return sendBuf2;
		}
                
		public ByteBuffer getRecvBuf() {
			return recvBuf;
		}

		public IbvSendWR getSendWR() {
			return sendWR;
		}

		public IbvRecvWR getRecvWR() {
			return recvWR;
		}
	}

}

