package com.linmo.nettydemo;

import com.linmo.nettydemo.netty.server.NettyServer;
import com.linmo.nettydemo.utils.PortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyDemoApplication {

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(NettyDemoApplication.class, args);
        InitNettyServer initNettyServer = new InitNettyServer();
        initNettyServer.setNettyServer(new NettyServer());
        initNettyServer.run(args);
    }

}
