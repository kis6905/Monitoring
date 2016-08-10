package com.btb.mmm.netty;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.btb.mmm.MonitoringMailManager;
import com.btb.mmm.vo.Mail;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class MMMServerHandler extends ChannelHandlerAdapter {
	
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String message = (String) msg;
		System.out.println("-> Message: " + message);
		
		JSONObject jsonResult = new JSONObject();
		boolean success = false;
		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);
			jsonObject.get("title");
			jsonObject.get("contents");
			jsonObject.get("responseCode");
			jsonObject.get("serviceCode");
			
			Mail mail = new Mail((String) jsonObject.get("title"), (String) jsonObject.get("contents"), (String) jsonObject.get("responseCode"), (String) jsonObject.get("serviceCode"));
			success = MonitoringMailManager.sendMail(mail);
		} catch (ParseException e) {
			e.printStackTrace();
			success = false;
		}
		jsonResult.put("success", success);
		
		ctx.write(jsonResult.toString());
		ctx.flush();
		ctx.close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
	
}
