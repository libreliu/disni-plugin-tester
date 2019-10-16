/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.storm;

/**
 * A fake config class.
 * @author libreliu
 */
public class Config {
    public static final String STORM_MESSAGING_TRANSPORT = "storm.messaging.transport";
    
    public static final String STORM_MESSAGING_DISNI_BINDING_FILTER_LIST = "storm.messaging.disni.binding.filter.list";
    
    public static final String STORM_MESSAGING_DISNI_BINDING_FILTER_ENABLE = "storm.messaging.disni.binding.filter.enable";
    
    public static final String STORM_MESSAGING_DISNI_BINDING_FILTER_STRICT = "storm.messaging.disni.binding.filter.strict";
    
    public static final String STORM_MESSAGING_DISNI_RECV_CALL_INITIALIZED = "storm.messaging.disni.recv.call.initialized";
    
    public static final String STORM_MESSAGING_DISNI_RECV_BUFFER_SIZE = "storm.messaging.disni.recv.buffer.size";
}
