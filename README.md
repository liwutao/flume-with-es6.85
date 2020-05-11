This edition is based on apache flume1.7.0.

I modifed code of elasticsearch sink model, in order to let flume work well with elastic6.8.

Change list:

1. Update all dependencies of es to 6.8
2. Rewrite invocation of es interface, according to description of newer interfaces.
3. Add "transport" dependency to support es 6.8 dependencies who used transport.
4. Add httpclient dependency to enable added REST interface of es.


Problems i had met:


1. FAIL_ON_SYMBOL_HASH_OVERFLOW


11 三月 2020 12:16:31,586 ERROR [lifecycleSupervisor-1-2] (org.apache.flume.lifecycle.LifecycleSupervisor$MonitorRunnable.run:251) - Unable to start SinkRunner: { policy:org.apache.flume.sink.DefaultSinkProcessor@29ce66c0 counterGroup:{ name:null counters:{} } } - Exception follows.
java.lang.NoSuchFieldError: FAIL_ON_SYMBOL_HASH_OVERFLOW
at org.elasticsearch.common.xcontent.json.JsonXContent.<clinit>(JsonXContent.java:57)
at org.elasticsearch.common.xcontent.XContentType$1.xContent(XContentType.java:56)
at org.elasticsearch.common.settings.Setting.arrayToParsableString(Setting.java:1318)
at org.elasticsearch.common.settings.Setting.access$800(Setting.java:87)
at org.elasticsearch.common.settings.Setting$ListSetting.lambda$new$0(Setting.java:1343)
at org.elasticsearch.common.settings.Setting$ListSetting.innerGetRaw(Setting.java:1353)
at org.elasticsearch.common.settings.Setting.getRaw(Setting.java:461)
at org.elasticsearch.common.settings.Setting.lambda$listSetting$35(Setting.java:1269)
at org.elasticsearch.common.settings.Setting.listSetting(Setting.java:1286)
at org.elasticsearch.common.settings.Setting.listSetting(Setting.java:1269)
at org.elasticsearch.transport.TransportSettings.<clinit>(TransportSettings.java:47)
at org.elasticsearch.client.transport.TransportClient.newPluginService(TransportClient.java:105)
at org.elasticsearch.client.transport.TransportClient.buildTemplate(TransportClient.java:135)
at org.elasticsearch.client.transport.TransportClient.<init>(TransportClient.java:288)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:128)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:114)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:104)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchTransportClient.openClient(ElasticSearchTransportClient.java:206)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchTransportClient.<init>(ElasticSearchTransportClient.java:79)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchClientFactory.getClient(ElasticSearchClientFactory.java:48)
at org.apache.flume.sink.elasticsearch.ElasticSearchSink.start(ElasticSearchSink.java:354)
at org.apache.flume.sink.DefaultSinkProcessor.start(DefaultSinkProcessor.java:45)
at org.apache.flume.SinkRunner.start(SinkRunner.java:79)
at org.apache.flume.lifecycle.LifecycleSupervisor$MonitorRunnable.run(LifecycleSupervisor.java:249)
at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)
at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)
at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
at java.lang.Thread.run(Thread.java:745)
11 三月 2020 12:16:31,590 INFO [lifecycleSupervisor-1-2] (org.apache.flume.sink.elasticsearch.ElasticSearchSink.stop:381) - ElasticSearch sink {} stopping



问题：所依赖jackson包版本不一致

解决：需要把本地打包使用的所有jackson包都替换到flume环境

 
2. ClassNotFound:io.netty.util.NettyRuntime

问题：缺少nettyCommon包

解决：把本地仓库netty目录下所有依赖包直接拷贝到flume环境

 

 
3. ClassNotFound:SslConfigurationLoader

问题：缺少elasticsearch-ssl-config包

解决方案：elasticsearch所有包都需要添加到flume

<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch-ssl-config</artifactId>
    <version>6.7.1</version>
</dependency>

 

4. ClassNotFound:SchemeIOSessionStrategy


unner: { policy:org.apache.flume.sink.DefaultSinkProcessor@6d310488 counterGroup:{ name:null counters:{} } } - Exception follows.
java.lang.NoClassDefFoundError: org/apache/http/nio/conn/SchemeIOSessionStrategy
at org.elasticsearch.index.reindex.ReindexPlugin.getSettings(ReindexPlugin.java:94)
at org.elasticsearch.plugins.PluginsService.lambda$getPluginSettings$0(PluginsService.java:89)
at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:267)
at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1374)
at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
at org.elasticsearch.plugins.PluginsService.getPluginSettings(PluginsService.java:89)
at org.elasticsearch.client.transport.TransportClient.buildTemplate(TransportClient.java:147)
at org.elasticsearch.client.transport.TransportClient.<init>(TransportClient.java:288)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:128)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:114)
at org.elasticsearch.transport.client.PreBuiltTransportClient.<init>(PreBuiltTransportClient.java:104)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchTransportClient.openClient(ElasticSearchTransportClient.java:206)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchTransportClient.<init>(ElasticSearchTransportClient.java:79)
at org.apache.flume.sink.elasticsearch.client.ElasticSearchClientFactory.getClient(ElasticSearchClientFactory.java:48)
at org.apache.flume.sink.elasticsearch.ElasticSearchSink.start(ElasticSearchSink.java:354)
at org.apache.flume.sink.DefaultSinkProcessor.start(DefaultSinkProcessor.java:45)
at org.apache.flume.SinkRunner.start(SinkRunner.java:79)
at org.apache.flume.lifecycle.LifecycleSupervisor$MonitorRunnable.run(LifecycleSupervisor.java:249)
at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)
at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)
at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.ClassNotFoundException: org.apache.http.nio.conn.SchemeIOSessionStrategy
at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
... 29 more



解决方案：httpasyncclient包需要拷贝到flume
