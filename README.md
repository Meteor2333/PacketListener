# PacketListener

[![Maven Central](https://img.shields.io/maven-central/v/cc.meteormc/packetlistener.svg)](https://central.sonatype.com/artifact/cc.meteormc/packetlistener)

[**English**](README.md) | [简体中文](README_CN.md)

### **This is an API for Bukkit plugins**

## Advantages

- Compared to other projects, this project wraps the packet interceptor and adds it directly to the ServerSocketChannel rather than listening for player join events and injecting interceptors through player instances. This approach enables capturing all packets from players regardless of their state. For example, packets sent when players query server information (latency, player count, MOTD) on the server list screen, as well as packets during client-server handshake and login.
- Provides a packet wrapper that makes it easy to obtain the packet’s stage, direction, and name, and also allows you to conveniently access the packet’s *position*-th field or the *position*-th field of a specific type, just like ProtocolLib.
- Includes cancellable events, so if you don’t want to receive or send a particular packet, you can cancel it through conventional means.

## Usage

**This project is published on [Maven Central](https://central.sonatype.com/artifact/cc.meteormc/packetlistener).**

### Maven

```
<dependencies>
    <dependency>
        <groupId>cc.meteormc</groupId>
        <artifactId>packetlistener</artifactId>
        <version>${version}</version>
    </dependency>
</dependencies>
```

### Gradle

```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'cc.meteormc:packetlistener:$version'
}
```

### Initialize it in the following way:

```
@Override
public void onEnable() {
    PacketListener.init(this);
    Bukkit.getPluginManager().registerEvents(this, this);
}

@EventHandler
public void onPacketInbound(PacketInboundEvent event) {
    //do something
}

@EventHandler
public void onPacketOutbound(PacketOutboundEvent event) {
    //do something
}
```

## Acknowledgements

1. [ProtocolLib (by dmulloy2)](https://github.com/dmulloy2/ProtocolLib/) provided some inspiration for this project.
2. [packetlistener (by miopowered)](https://github.com/miopowered/packetlistener) was the motivation behind this project — its cancellable events did not behave as expected during usage.