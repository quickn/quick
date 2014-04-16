# -*- coding: utf8 -*-
#实现队列的保证可靠性的一个监控工具，他会定时扫描redis队列，对于超时没有确认的队列数据放回到工作队列中去。
# designed by libaijun. 2012-09-17
import redis
import time
import logging
import logging.handlers

logger = logging.getLogger('Queue_Tools')
logger.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler = logging.handlers.RotatingFileHandler(
              '/home/43gz/logs/Queue_Tools.log', maxBytes=1024*1024*50, backupCount=5)
handler.setFormatter(formatter);
logger.addHandler(handler);

host="localhost"
port=6379
r = redis.StrictRedis(host=host, port=port, db=0)
queueNames = ['fkpmInQueue','longtailQueue','TRANSITDATA','LISTINGNUMIIDUSERID']
def recallQueue(queueName):
    todo = "quickj:queue:" + queueName + ":todo";
    todoData = "quickj:queue:" + queueName + ":todo_data";
    doing = "quickj:queue:" + queueName + ":doing";
    lease = "quickj:queue:" + queueName + ":doing_lease";
    pipe = r.pipeline(transaction=False)
    todoLen = pipe.llen(todo);
    doingLen = pipe.llen(doing);
    leaseLen = pipe.llen(lease);
    preturn = pipe.execute();
    logger.info(queueName+"队列长度："+str(preturn[0])+",正在执行的数量为："+str(preturn[2])+"已经超时需要恢复的为："+str(preturn[1] - preturn[2]))

    keys = r.lrange(doing,0,-1)
    for key in keys:
        if r.exists(lease+key)==False:
            logger.info('恢复Job，id：'+key);
            r.lrem(doing, -1, key);
            r.lpush(todo, key);
while True:
    try:
        for queueName in queueNames:
            recallQueue(queueName)
    except Exception, e:
        print e
        r = redis.StrictRedis(host=host, port=port, db=0)
    #等待30miao后继续继续检查。
    time.sleep(30);
