package com.buyuphk.soketcommunication.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;


import com.buyuphk.soketcommunication.MyApplication;
import com.buyuphk.soketcommunication.NettyConstant;
import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;
import com.google.protobuf.InvalidProtocolBufferException;
import com.buyuphk.soketcommunication.codec.DefConf;
import com.buyuphk.soketcommunication.codec.NetCmdDataHeartDP;
import com.buyuphk.soketcommunication.codec.NetCommand;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class NodeClientHandler extends SimpleChannelInboundHandler<NetCommand> {
	private static final String TAG = NodeClientHandler.class.getSimpleName();
	private Channel channelHandlerContext;
	private Context context;

	public NodeClientHandler(Context context) {
		this.context = context;
	}
	/**
	 * channelOpen、channelBound和channelConnected被合并为channelActive。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Log.d(TAG, "channelActive");
		Intent intent = new Intent("jianfei");
		byte type = 0;
		intent.putExtra("type", type);
		intent.putExtra("isActive", true);
		context.sendBroadcast(intent);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		Intent intent = new Intent("jianfei");
		byte type = 0;
		intent.putExtra("type", type);
		intent.putExtra("isActive", false);
		context.sendBroadcast(intent);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		Intent intent = new Intent("jianfei");
		byte type = 0;
		intent.putExtra("type", type);
		intent.putExtra("isActive", false);
		context.sendBroadcast(intent);
	}

	/**
	 * Now, if you are using Netty 4.X to read message use the method
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Log.d(TAG, "channelRead");
		super.channelRead(ctx, msg);
		this.channelHandlerContext = ctx.channel();
		NetCommand cmd = (NetCommand) msg;
		System.out.println("channelRead:接受到消息111"+cmd.getCmdCode());
		System.out.println("channelRead:接受到消息222"+cmd.toString());
		System.out.println("channelRead:消息类型===> : {}"+ cmd.getCmdType());
		// 心跳类型
		if (DefConf.HEART_COMMAND == cmd.getCmdType()) {
			// 一个心跳包发送过来了
			handleHeartMsg(ctx,cmd);
		} else { // 其他命令类型
			handleData(ctx,cmd);
		}
	}

	/**
	 * channelRead0 is from SimpleChannelInboundHandler of 4.x, and it will be renamed to messageReceived in Netty 5
	 * @param channelHandlerContext
	 * @param netCommand
	 * @throws Exception
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, NetCommand netCommand) throws Exception {
//		Log.d(TAG, "channelRead0");
//		Log.d(TAG, "channelRead0:" + netCommand.getVersion());
//		Log.d(TAG, "channelRead0:" + netCommand.getCmdId());
//		Log.d(TAG, "channelRead0:" + netCommand.getCmdCode());
//		Log.d(TAG, "channelRead0:" + netCommand.getCmdRes());
//		Log.d(TAG, "channelRead0:" + netCommand.getCmdResMsg());
//		Log.d(TAG, "channelRead0:" + netCommand.getData());
//		Log.d(TAG, "channelRead0:" + netCommand.getCmdType());

	}

	/**
	 * 进入和出去的正常数据的处理
	 * 
	 * @param ctx
	 * @param
	 */
	protected void handleData(ChannelHandlerContext ctx, NetCommand msg) {
		System.out.println("handleData 处理到达数据");
//		Intent intent = new Intent("voice");
//		byte type = 1;
//		intent.putExtra("type", type);
//		intent.putExtra("message", msg.getData());
//		context.sendBroadcast(intent);
		Intent intent = new Intent("customer_service_message");
		intent.putExtra("message", msg.getData());
		intent.putExtra("fromWho", msg.getCmdId());
		if (msg.getCmdCode().equals("0")) {
			intent.putExtra("messageType", 0);
		} else {
			intent.putExtra("messageType", 1);
		}
		context.sendBroadcast(intent);
	}

    /**
     * 处理心跳响应信息
     * 
     * 客户端的场合 不要再响应信息了,会造成死循环
     * 
     * @param context
     */
    private void handleHeartMsg(ChannelHandlerContext context,NetCommand msg) {
    	//log.debug("处理心跳响应信息");
    	// 客户端响应处理心跳响应
    	if (null != msg 
    			&& DefConf.HEART_COMMAND == msg.getCmdType() 
    			&& "CT00000001".equals(msg.getCmdCode())) {
    		
    		// 获得参数的数据
    		byte[] heartDtoByts = msg.getData();
    		NetCmdDataHeartDP.NetCmdDataHeartDto heartDtoParam = null;
    		try {
    			// 反序列化得到的命令参数 用户名和密码对象
    			heartDtoParam = NetCmdDataHeartDP.NetCmdDataHeartDto.parseFrom(heartDtoByts);
    			
    			String heartTime = heartDtoParam.getTime();
    			
    			//log.debug("处理心跳响应信息处理:"+context.channel().remoteAddress() + heartTime);
				/**
				 * 2022-07-23 14:06:08 编写把心跳信息写入本地SQLite数据库
				 */
				MySQLiteOpenDatabase mySQLiteOpenDatabase = MyApplication.instance.getMySQLiteOpenDatabase();
				android.database.sqlite.SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
				android.content.ContentValues contentValues = new android.content.ContentValues(2);
				contentValues.put("log_content", heartDtoParam.toString());
				contentValues.put("heart_time", heartTime);
				long result = sqLiteDatabase.insert("log", null, contentValues);
				android.util.Log.d("debug", "打印插入心跳信息到本地数据库返回的结果->" + result);
    		} catch (InvalidProtocolBufferException e) {
    			System.out.println("E00000001001-execute 反序列失败：" + e.getMessage());

    		}
    	}
    	// 客户端不做任何响应心跳的处理
    	System.out.println("处理心跳响应信息完成"+context.channel().remoteAddress());
    }

    public void sendLoginMsg(final Channel channel) {
		channelHandlerContext = channel;
		if (channel == null) {
			System.out.println("----管道为空------");
			return;
		}
		NetCommand netCommand = new NetCommand();
		netCommand.setVersion(2);
		// 命令Id
		netCommand.setCmdId(NettyConstant.CLIENT_ID);
		// 命令类型(心跳)
		netCommand.setCmdType(DefConf.LOGIN_COMMAND);
		// 命令编号(CT00000002 客户端向服务器端发送心跳包)
		netCommand.setCmdCode("CT00000002");
		channel.writeAndFlush(netCommand);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					while (true) {
//						Thread.sleep(2000);
//						NetCommand netCommand = new NetCommand();
//						netCommand.setVersion(2);
//						// 命令Id
//						netCommand.setCmdId("00000002");
//						// 命令类型(心跳)
//						netCommand.setCmdType(DefConf.MQ_COMMAND);
//						// 命令编号(CT00000002 客户端向服务器端发送心跳包)
//						netCommand.setCmdCode("CT00000002");
//						channel.writeAndFlush(netCommand);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();

	}
	/**
	 * 向客户端发送心跳包
	 * 
	 * @param context
	 */
    protected void sendPingMsg(ChannelHandlerContext context) {
    	Log.d(TAG, "向服务器端发送心跳包");
    	// 准备发送命令
		NetCommand netCommand = new NetCommand();
		netCommand.setVersion(2);
		// 命令Id
		netCommand.setCmdId("00000002");
		// 命令类型(心跳)
		netCommand.setCmdType(DefConf.HEART_COMMAND);
		// 命令编号(CT00000002 客户端向服务器端发送心跳包)
		netCommand.setCmdCode("CT00000002");
		// 命令参数
		NetCmdDataHeartDP.NetCmdDataHeartDto.Builder netCmdDataHeartDtoBuilder 
			= NetCmdDataHeartDP.NetCmdDataHeartDto.newBuilder();
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
		// new Date()为获取当前系统时间
		String nowTime = df.format(System.currentTimeMillis());
		netCmdDataHeartDtoBuilder.setTime(nowTime);
		// 21 服务器 到 客户端  12 客户端 到 服务器
		netCmdDataHeartDtoBuilder.setDirection(12);
		
		NetCmdDataHeartDP.NetCmdDataHeartDto netCmdDataHeartDto 
			= netCmdDataHeartDtoBuilder.build();
		netCommand.setData(netCmdDataHeartDto.toByteArray());
		context.channel().writeAndFlush(netCommand);
    }
    
    
    /**
	 * 空闲事件处理
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Log.d(TAG, "userEventTriggered");
		Intent intent = new Intent("jianfei");
		byte type = 2;
		intent.putExtra("type", type);
		context.sendBroadcast(intent);
		// IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                	Log.d("debug", "阅读器空闲");
                    //handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                	Log.d("debug", "写入器空闲");
                    //handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                	Log.d("debug", "所有空闲");
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
	}
	
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
        Log.d(TAG, "---READER_IDLE---");
        Log.d(TAG, "---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        // 客户端连接超时,关闭与客户端的连接
		Log.d(TAG, "客户端连接超时,关闭与客户端的连接");
        ctx.close();
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
    	Log.d(TAG, "---WRITER_IDLE---");
    }

    /**
     * 客户端处理空闲
     * 只要有空闲下来 就隔段时间发送心跳包
     * @param ctx
     */
    protected void handleAllIdle(ChannelHandlerContext ctx) {
    	Log.d(TAG, "---ALL_IDLE---");
    	// 向服务器端发送心跳包
		Log.d(TAG, "向服务器端发送心跳包");
		 sendPingMsg(ctx);
    }
}
