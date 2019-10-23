package com.sun.api.thread;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: ActionPostQueue.java
 * Author: wds_sun
 * Date: 2019-10-23 16:25
 * Description:
 */
public class ActionPostQueue {

    private ActionPost head;

    private ActionPost tail;

    synchronized void enqueue(ActionPost pendingPost){
        if(pendingPost==null) {
            throw new NullPointerException("null cannot be enqueued");
        }

        if(tail!=null) {
            tail.next=pendingPost;
            tail=pendingPost;
        }else if(head==null) {
            head=tail=pendingPost;
        }else {
            throw new IllegalStateException("Head present, but no tail");
        }
        notifyAll();

    }


    synchronized ActionPost poll(){
       ActionPost pendingPost=head;

       if(head!=null) {
           head=head.next;

           if(head==null) {
               tail=null;
           }
       }
       return pendingPost;
    }

    synchronized ActionPost poll(int maxMillisToWait) throws InterruptedException {
        if (head == null) {
            wait(maxMillisToWait);
        }
        return poll();
    }

}
