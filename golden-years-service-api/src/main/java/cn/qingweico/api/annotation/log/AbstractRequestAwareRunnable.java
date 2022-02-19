package cn.qingweico.api.annotation.log;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 异步记录日志
 * @author:qiming
 * @date: 2021/11/10
 */
public abstract class AbstractRequestAwareRunnable implements Runnable {
   private final RequestAttributes requestAttributes;
   private Thread thread;
   public AbstractRequestAwareRunnable() {
      this.requestAttributes = RequestContextHolder.getRequestAttributes();
      this.thread = Thread.currentThread();
   }
   @Override
   public void run() {
      try {
         RequestContextHolder.setRequestAttributes(requestAttributes);
         onRun();
      } finally {
         if (Thread.currentThread() != thread) {
            RequestContextHolder.resetRequestAttributes();
         }
         thread = null;
      }
   }

   /**
    * 启动
    */
   protected abstract void onRun();
}
