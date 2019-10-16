/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.ustc.acsa.test;

/**
 *
 * @author libreliu
 */
public class TestMain {

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("args[0] should be client/server, abort");
            return;
        }
        
        // ** args[0] == server simply won't work
        if (args[0].equals("server")) {
            String argPass[] = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                argPass[i - 1] = args[i];
            }
            
            ServerRunner.main(argPass);
        } else if (args[0].equals("client")) {
            String argPass[] = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                argPass[i - 1] = args[i];
            }
            
            ClientRunner.main(argPass);
        } else {
            System.out.println("args[0] should be client/server (got: " + args[0] + "), abort");
            return;
        }
    }
}
