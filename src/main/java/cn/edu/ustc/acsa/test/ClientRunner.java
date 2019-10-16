/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.ustc.acsa.test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.messaging.IConnection;
import org.apache.storm.messaging.IConnectionCallback;
import org.apache.storm.messaging.IContext;
import org.apache.storm.messaging.TaskMessage;
import org.apache.storm.messaging.TransportFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author libreliu
 */
public class ClientRunner {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ClientRunner.class);


    public static void main(String args[]) {
        /* Generate fake configuration */
        Map<String, Object> fakeConf = new HashMap<>();
        fakeConf.put("storm.messaging.transport", "org.apache.storm.messaging.disni.Context");

        /* Instantiate a server and then bind to a port */
        IContext serverPluginContext = TransportFactory.makeContext(fakeConf);

        AtomicBoolean[] foo = new AtomicBoolean[5];

        IConnection conn = serverPluginContext.connect("1", args[0], Integer.parseInt(args[1]), foo);
        
        LinkedList<TaskMessage> msgList = new LinkedList<>();
        
        TaskMessage msgHi = new TaskMessage(1, 
                "Hi from ClientRunner!!".getBytes(Charset.forName("UTF-8")));
        
        
        msgList.add(msgHi);
        
        for (int i = 0; i < 100; i++) {
            conn.send(msgList.iterator());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                LOG.warn("got {}", ex.toString());
            }
        }
    }
}
