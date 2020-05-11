package org.apache.flume.sink.elasticsearch.client;

/**
 * describe
 *
 * @author liwutao
 * @date 2020/03/09
 */
public class ElasticSearchClientFailedException extends Exception {
    public ElasticSearchClientFailedException() {
        super();
    }
    public ElasticSearchClientFailedException(String msg) {
        super(msg);
    }
    public ElasticSearchClientFailedException(String msg, Throwable e) {
        super(msg, e);
    }
}
