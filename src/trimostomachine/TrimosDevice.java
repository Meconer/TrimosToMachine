/*
 * Copyright (C) 2016 Mats Andersson <mats.andersson@mecona.se>.
 *
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this code; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package trimostomachine;

import com.google.common.eventbus.EventBus;
import static java.lang.Thread.sleep;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 *
 * This class handles the Trimos device
 *
 * 
 */
public class TrimosDevice {

    private final SerialCommHandler serialCommHandler;

    private final EventBus eventBus;
 
     public TrimosDevice(EventBus eventBus) {
        serialCommHandler = new SerialCommHandler();
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public void startTrimosConnection() {
        initMessageReceiverTask();
        serialCommHandler.startReader();
    }

    private static final int LOOP_TIME = 500;
    private volatile boolean messageReceiverTaskStopped = false;
    private MessageReceiverTask messageReceiverTask;
    private boolean stopMessageReceiverTask;
    private double latestXValue = 0;
    private double latestZValue = 0;

    private void initMessageReceiverTask() {
        messageReceiverTask = new MessageReceiverTask();
        stopMessageReceiverTask = false;
        Thread mRT = new Thread(messageReceiverTask);
        mRT.setDaemon(true);
        mRT.start();
    }

    private class MessageReceiverTask implements Runnable {

        @Override
        public void run() {
            String previousMessage = null;
            while (!stopMessageReceiverTask) {
                String message = serialCommHandler.getMessageFromReceiveQueue();

                if (message != null) {
                        checkMessage(message);
                }
                try {
                    sleep(LOOP_TIME);
                } catch (InterruptedException ex) {

                }
                //System.out.println("MessageReceiverTask is running");
            }
            messageReceiverTaskStopped = true;

        }

        private void checkMessage(String message) {
            System.out.println("CheckMessage :" + message);
            latestXValue = getXFromMessage(message);
            latestZValue = getZFromMessage(message);
            System.out.println("xVal : " + latestXValue);
            System.out.println("zVal : " + latestZValue);
            sendMessageToGui(new PositionMessage(latestXValue, latestZValue));
        }

        private void sendMessageToGui(PositionMessage message) {
            Platform.runLater(() -> {
                eventBus.post(message);
            });
        }

        private double getXFromMessage(String message) {
            double xValue = -999.999;
            
            Pattern xPattern = Pattern.compile("Xr +([-|\\+]*\\d+.\\d+)");
            Matcher m = xPattern.matcher(message);
            if ( m.find()) {
                String xString = m.group(1);
                xValue = Double.parseDouble(xString);
            }
            return xValue;
        }

        private double getZFromMessage(String message) {
            double zValue = -999.999;
            
            Pattern zPattern = Pattern.compile("Zr +([-|\\+]*\\d+.\\d+)");
            Matcher m = zPattern.matcher(message);
            if ( m.find()) {
                String zString = m.group(1);
                zValue = Double.parseDouble(zString);
            }
            return zValue;
        }

    }

    
    void stopThreads() {
        serialCommHandler.stopReader();
        stopMessageReceiverTask = true;
        while (!messageReceiverTaskStopped) {
            // Wait for task stop;
        }

    }

}
