package com.myz.rocketmq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testContextLoads() throws Exception{
        //创建一个生产者
        DefaultMQProducer producer = new DefaultMQProducer("test-producer-group");
        //连接namesrv
        producer.setNamesrvAddr("192.168.5.72:9876");
        //启动生产者
        producer.start();

        Message message = new Message("test-topic","我是一个简单的消息".getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(message);
        SendStatus sendStatus = result.getSendStatus();
        System.out.println(sendStatus);
        //关闭生产者
        producer.shutdown();
    }
}
