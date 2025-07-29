# PacketListener

[![Maven Central](https://img.shields.io/maven-central/v/cc.meteormc/packetlistener.svg)](https://central.sonatype.com/artifact/cc.meteormc/packetlistener)

[English](README.md) | [**简体中文**](README_CN.md)

## 有什么优点？

- 相较于其他项目，本项目将网络数据包拦截器包装后直接添加到了 ServerSocketChannel，而不是监听玩家加入游戏然后从玩家实例注入拦截器。这样的好处是使得玩家在任何状态下都可以完全捕捉到所有的数据包，例如玩家在服务器列表界面查询服务器信息（延时、玩家数、MOTD）的数据包，还有客户端与服务端握手、登录时的数据包。
- 提供了数据包包装器，可以很方便的获取数据包的阶段、方向、名称，并且允许通过像 ProtocolLib 那样的方式获取数据包中的第 *position* 个字段和第 *position* 个类型为 *type* 的字段。
- 包含可取消事件，如果你不想接收或发送某个特定的数据包，可以通过 Bukkit 常规的方法取消它。

## 如何使用？

本项目已发布至 [Maven Central](https://central.sonatype.com/artifact/cc.meteormc/packetlistener)。

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

## 鸣谢

1. [ProtocolLib（by dmulloy2）](https://github.com/dmulloy2/ProtocolLib/) 给本项目提供了一些思路。
2. [packetlistener（by miopowered）](https://github.com/miopowered/packetlistener) 是本项目的动机，因为它的可取消事件未按预期工作。