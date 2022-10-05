package com.linmo.nettydemo;

import com.linmo.nettydemo.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class InitNettyServer implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    @Autowired
    public void setNettyServer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }
}
