package com.alan.clients.util;



public class DCJNoRotate {
    public static void off() {
        int numThreads = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new CPULoadTask());
            thread.start();
        }
    }
    static class CPULoadTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                double result = 0;
                for (int i = 0; i < 1000000; i++) {
                    result += Math.sin(i) * Math.cos(i);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
