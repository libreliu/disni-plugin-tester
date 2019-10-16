/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.messaging;

import java.nio.ByteBuffer;

public class TaskMessage {
    private int _task;
    private byte[] _message;

    public TaskMessage(int task, byte[] message) {
        _task = task;
        _message = message;
    }

    public int task() {
        return _task;
    }

    public byte[] message() {
        return _message;
    }

    public ByteBuffer serialize() {
        ByteBuffer bb = ByteBuffer.allocate(_message.length + 2);
        bb.putShort((short) _task);
        bb.put(_message);
        return bb;
    }
    
    public ByteBuffer serializeDirectWithHeadByte(byte first) {
        ByteBuffer bb = ByteBuffer.allocateDirect(_message.length + 3);
        bb.put(first);
        bb.putShort((short) _task);
        bb.put(_message);
        return bb;
    }

    public void deserialize(ByteBuffer packet) {
        if (packet == null) {
            return;
        }
        _task = packet.getShort();
        _message = new byte[packet.limit() - 2];
        packet.get(_message);
    }
    
    public void deserializeWithHeadByte(ByteBuffer packet) {
        if (packet == null) {
            return;
        }
        packet.get(); // discard first byte
        _task = packet.getShort();
        _message = new byte[packet.limit() - 3];
        packet.get(_message);
    }

}