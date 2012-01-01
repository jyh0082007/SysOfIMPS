/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     logic action handler
 */





package com.imps.server.handler;

import java.sql.SQLException;

import com.imps.server.base.CommandId;
import com.imps.server.base.InputMessage;
import com.imps.server.main.AddFriendReq;
import com.imps.server.main.AddFriendRsp;
import com.imps.server.main.FriendListRequest;
import com.imps.server.main.HeartBeat;
import com.imps.server.main.Login;
import com.imps.server.main.Register;
import com.imps.server.main.SendAudio;
import com.imps.server.main.SendMessage;
import com.imps.server.main.SendPTPAudioReq;
import com.imps.server.main.SendPTPAudioRsp;
import com.imps.server.main.SendPTPVideoReq;
import com.imps.server.main.SendPTPVideoRsp;
import com.imps.server.net.IoHandlerAdapter;
import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;


public class LogicHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, NetMessage msg) {
		
		InputMessage inMsg = (InputMessage) msg;
		switch(inMsg.getCmdType()) {
		case CommandId.C_LOGIN_REQ:
			/**�ͻ��˵�¼����*/
			System.out.println("server:login request received!");
            try {
				new Login(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.C_HEARTBEAT_REQ:
			/** �������ͻ�������,ͬʱ���յ���λ����Ϣ */
			System.out.println("server: heartbeat received!");
			try {
				new HeartBeat(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.C_FRIENDLIST_REFURBISH_REQ:
			/**�����б�ˢ������*/
			System.out.println("server: friend list refresh");
			try {
				new FriendListRequest(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.C_ADDFRIEND_REQ:
		/**�ͻ�����Ӻ�������*/
			System.out.println("server:add friend request receive");
			try {
				new AddFriendReq(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.C_ADDFRIEND_RSP:
			/** �ͻ�����Ӻ�����Ӧ */
			System.out.println("server:add friend response receive");
			try {
				new AddFriendRsp(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.C_SEND_MSG:
			/**�ͻ��˷�����Ϣ*/
			System.out.println("server: message from client to friend received");
			try {
				new SendMessage(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.C_REGISTER:
			/** �ͻ����û�ע��*/
			try {
				new Register(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.C_AUDIO_REQ:
			/**�ͻ�����Ƶ�ļ�����**/
			try {
				System.out.println(" audio data received");
				new SendAudio(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.C_PTP_AUDIO_REQ:
			try {
				new SendPTPAudioReq(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.C_PTP_AUDIO_RSP:
			try {
				new SendPTPAudioRsp(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.C_PTP_VIDEO_REQ:
			try {
				new SendPTPVideoReq(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.C_PTP_VIDEO_RSP:
			try {
				new SendPTPVideoRsp(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
