package com.btb.bcdsm.netty;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.btb.bcdsm.BCDSM;
import com.btb.bcdsm.event.Event;

public class MMMClientHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String message = (String) msg;
	    
	    try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);
			BCDSM.eventHandler(new Event(Event.MMM_RESULT, (Boolean) jsonObject.get("success")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
