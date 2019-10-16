package cn.edu.ustc.acsa.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.storm.messaging.TransportFactory;
import org.slf4j.LoggerFactory;

import java.util.Map;
import org.apache.storm.messaging.IConnection;
import org.apache.storm.messaging.IConnectionCallback;
import org.apache.storm.messaging.IContext;
import org.apache.storm.messaging.TaskMessage;

/**
 * Standalone tester for storm messaging plugin.
 *
 * @author libreliu
 */
public class ServerRunner {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ServerRunner.class);

    public static class RecvHandler implements IConnectionCallback {

        @Override
        public void recv(List<TaskMessage> batch) {
            LOG.info("Enter recv() call");
            for (Iterator<TaskMessage> it = batch.iterator(); it.hasNext();) {
                TaskMessage taskmsg = it.next();
                LOG.info("Got message: {}", new String(taskmsg.message(), Charset.forName("UTF-8")));
                // it.remove(); // optional
            }
        }

    }

    public static void main(String args[]) {
        /* Generate fake configuration */
        Map<String, Object> fakeConf = new HashMap<>();
        fakeConf.put("storm.messaging.transport", "org.apache.storm.messaging.disni.Context");
        List<String> fakeList = new ArrayList<>();
        fakeList.add("vir");
        fakeList.add("docker");
        fakeList.add("tun0");
        fakeConf.put("storm.messaging.disni.binding.filter.list", fakeList);
        fakeConf.put("storm.messaging.disni.binding.filter.enable", true);
        fakeConf.put("storm.messaging.disni.binding.filter.strict", false);
        fakeConf.put("storm.messaging.disni.recv.call.initialized", 50);
        fakeConf.put("storm.messaging.disni.recv.buffer.size", "1048576"); // Test different input (String and pure Number)
        
        /* Instantiate a server and then bind to a port */
        IContext serverPluginContext = TransportFactory.makeContext(fakeConf);

        RecvHandler recv_handler = new RecvHandler();

        IConnection conn = serverPluginContext.bind("1", 14514); // e chou duan kou (114514, yeah)
        conn.registerRecv(recv_handler);
    }

}
