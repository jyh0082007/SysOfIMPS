package com.imps.net.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.CommandId;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.events.IAudioEvent;
import com.imps.events.IAudioEventDispacher;
import com.imps.events.IConnEvent;
import com.imps.events.IConnEventDispacher;
import com.imps.events.IContactEvent;
import com.imps.events.IContactEventDispacher;
import com.imps.events.ILoginEvent;
import com.imps.events.ILoginEventDispacher;
import com.imps.events.IRegisterEvent;
import com.imps.events.IRegisterEventDispacher;
import com.imps.events.ISmsEvent;
import com.imps.events.ISmsEventDispacher;
import com.imps.events.IVideoEvent;
import com.imps.events.IVideoEventDispacher;
import com.imps.services.impl.ServiceManager;

public class NetMsgLogicHandler extends SimpleChannelUpstreamHandler implements ISmsEventDispacher
,IAudioEventDispacher,IVideoEventDispacher,IContactEventDispacher,IRegisterEventDispacher,ILoginEventDispacher,IConnEventDispacher{
	
	private static String TAG = NetMsgLogicHandler.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private byte cmdType;
	private List<IConnEvent> mConnEvntList;
	private List<ISmsEvent> mSmsEvntList;
	private List<IAudioEvent> mAudioEvntList;
	private List<IVideoEvent> mVideoEvntList;
	private List<IContactEvent> mContactEvntList;
	private List<IRegisterEvent> mRegEvntList;
	private List<ILoginEvent> mLoginEvntList;
	
	public NetMsgLogicHandler(){
		mConnEvntList = new ArrayList<IConnEvent>();
		mSmsEvntList = new ArrayList<ISmsEvent>();
		mAudioEvntList = new ArrayList<IAudioEvent>();
		mVideoEvntList = new ArrayList<IVideoEvent>();
		mContactEvntList = new ArrayList<IContactEvent>();
		mRegEvntList = new ArrayList<IRegisterEvent>();
		mLoginEvntList = new ArrayList<ILoginEvent>();
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) Log.d(TAG,"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
    	for(int i=0;i<mConnEvntList.size();i++){
    		if(mConnEvntList.get(i)!=null)
    		mConnEvntList.get(i).onDisconnected();
    	}
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) Log.d(TAG,"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    	for(int i=0;i<mConnEvntList.size();i++){
    		if(mConnEvntList.get(i)!=null)
    		mConnEvntList.get(i).onConnected();
    	}
    }
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
	{
		Channel session = e.getChannel();
		ChannelBuffer inMsg =(ChannelBuffer)e.getMessage();
		cmdType = inMsg.readByte();
		switch(cmdType) {
		case CommandId.S_ERROR:
			if(DEBUG){Log.d(TAG,"error msg recv.");}
			
				int errorCode = inMsg.readInt();
				int detailCode = 0;
				switch(errorCode){
				case 1://login error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1:
						//username or password not valid
						if(DEBUG) Log.d(TAG,"Login error:username or password not valid.");
						for(int i=0;i<mConnEvntList.size();i++){
							if(mConnEvntList.get(i)==null){
								continue;
							}
							mLoginEvntList.get(i).onLoginError(IMPSDev.getContext().getResources().getString(R.string.login_username_password_error),detailCode);
						}
						break;
					case 2://username login at other place
						if(DEBUG) Log.d(TAG,"Login error:account logins at other place.");
						for(int i=0;i<mConnEvntList.size();i++){
							if(mConnEvntList.get(i)==null){
								continue;
							}
							mLoginEvntList.get(i).onLoginError(IMPSDev.getContext().getResources().getString(R.string.login_account_at_other_place),detailCode);
						}
						break;
					case 3://other error
						if(DEBUG) Log.d(TAG,"Unknown login error.");
						for(int i=0;i<mConnEvntList.size();i++){
							if(mConnEvntList.get(i)==null){
								continue;
							}
							mLoginEvntList.get(i).onLoginError(IMPSDev.getContext().getResources().getString(R.string.login_unknown_error),detailCode);
						}
						break;
					default:
						break;
					}
					break;
				case 2://register error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1://username already exists
						if(DEBUG) Log.d(TAG,"Reg error: username already exists");
						for(int i=0;i<mRegEvntList.size();i++){
							if(mRegEvntList.get(i)==null){
								continue;
							}
							mRegEvntList.get(i).onRegError(IMPSDev.getContext().getResources().getString(R.string.register_user_exist),
									detailCode);
						}
						break;
					case 2://other error
						if(DEBUG) Log.d(TAG,"Reg error: unknown");
						for(int i=0;i<mRegEvntList.size();i++){
							if(mRegEvntList.get(i)==null){
								continue;
							}
							mRegEvntList.get(i).onRegError(IMPSDev.getContext().getResources().getString(R.string.register_fail),detailCode);
						}
						break;
					}
					break;
				case 3://send sms error
					detailCode = inMsg.readInt();
					int smsId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) Log.d(TAG,"Send sms success:"+smsId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onSmsSendSuccess(IMPSDev.getContext().getResources().getString(R.string.sms_send_success), smsId);
						}
						break;
					case 2://friend not exists or not online
						if(DEBUG) Log.d(TAG,"Send sms fail(friend not exists or offline):"+smsId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onSmsSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_friend_not_login), smsId);
						}
						break;
					case 3://friend rejects
						if(DEBUG) Log.d(TAG,"Send sms fail(friend rejects):"+smsId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onSmsSendFail(IMPSDev.getContext().getResources().getString(R.string.friend_reject), smsId);
						}
						break;
					case 4://other error
						if(DEBUG) Log.d(TAG,"Send sms fail(unknown error):"+smsId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onSmsSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_send_fail), smsId);
						}
						break;
					}
					break;
				case 4://send image error
					detailCode = inMsg.readInt();
					int imageId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) Log.d(TAG,"Send image success:"+imageId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendSuccess(IMPSDev.getContext().getResources().getString(R.string.image_send_success), imageId);
						}
						break;
					case 2://friend not exists or not online
						if(DEBUG) Log.d(TAG,"Send image fail(friend not exists or offline):"+imageId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_friend_not_login), imageId);
						}
						break;
					case 3://friend rejects
						if(DEBUG) Log.d(TAG,"Send image fail(friend rejects):"+imageId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.friend_reject), imageId);
						}
						break;
					case 4://other error
						if(DEBUG) Log.d(TAG,"Send image fail(unknown error):"+imageId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_send_fail), imageId);
						}
						break;
					}
					break;
				case 5://send audio error
					detailCode = inMsg.readInt();
					int audioId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) Log.d(TAG,"Send audio success:"+audioId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onAudioSendSuccess(IMPSDev.getContext().getResources().getString(R.string.image_send_success), audioId);
						}
						break;
					case 2://friend not exists or not online
						if(DEBUG) Log.d(TAG,"Send audio fail(friend not exists or offline):"+audioId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_friend_not_login), audioId);
						}
						break;
					case 3://friend rejects
						if(DEBUG) Log.d(TAG,"Send audio fail(friend rejects):"+audioId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.friend_reject), audioId);
						}
						break;
					case 4://other error
						if(DEBUG) Log.d(TAG,"Send audio fail(unknown error):"+audioId);
						for(int i=0;i<mSmsEvntList.size();i++){
							if(mSmsEvntList.get(i)==null){
								continue;
							}
							mSmsEvntList.get(i).onImageSendFail(IMPSDev.getContext().getResources().getString(R.string.sms_send_fail), audioId);
						}
						break;
					
					}
					break;
				case 6: //p2p audio request error
					detailCode = inMsg.readInt();
					int p2paudioId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) Log.d(TAG,"p2p audio req sent success.");
						for(int i=0;i<mAudioEvntList.size();i++){
							if(mAudioEvntList.get(i)==null){
								continue;
							}
							mAudioEvntList.get(i).onP2PAudioReqSuccess();
						}
						break;
					case 2://friend not exists or not online
						if(DEBUG) Log.d(TAG,"p2p audio req sent faile:friend offline");
						for(int i=0;i<mAudioEvntList.size();i++){
							if(mAudioEvntList.get(i)==null){
								continue;
							}
							mAudioEvntList.get(i).onP2PAudioError(
									IMPSDev.getContext().getResources().getString(R.string.friend_offline),detailCode);
						}
						break;
					case 3://friend reject
						if(DEBUG) Log.d(TAG,"p2p audio req sent fail:friend reject");
						for(int i=0;i<mAudioEvntList.size();i++){
							if(mAudioEvntList.get(i)==null){
								continue;
							}
							mAudioEvntList.get(i).onP2PAudioError(
									IMPSDev.getContext().getResources().getString(R.string.friend_reject),detailCode);
						}
						break;
					case 4://net error
						if(DEBUG) Log.d(TAG,"p2p audio req sent faile:net error");
						for(int i=0;i<mAudioEvntList.size();i++){
							if(mAudioEvntList.get(i)==null){
								continue;
							}
							mAudioEvntList.get(i).onP2PAudioError(
									IMPSDev.getContext().getResources().getString(R.string.net_error),detailCode);
						}
						break;
					case 5://other error
						if(DEBUG) Log.d(TAG,"p2p audio req sent faile:unknown error");
						for(int i=0;i<mAudioEvntList.size();i++){
							if(mAudioEvntList.get(i)==null){
								continue;
							}
							mAudioEvntList.get(i).onP2PAudioError(
									IMPSDev.getContext().getResources().getString(R.string.net_error),detailCode);
						}
						break;
					}
					break;
				case 7: //p2p video request error
					detailCode = inMsg.readInt();
					int p2pvideoId = inMsg.readInt();
					switch(detailCode){

					case 1://send success
						if(DEBUG) Log.d(TAG,"p2p video req sent success.");
						for(int i=0;i<mVideoEvntList.size();i++){
							if(mVideoEvntList.get(i)==null){
								continue;
							}
							mVideoEvntList.get(i).onP2PVideoReqSuccess();
						}
						break;
					case 2://friend not exists or not online
						if(DEBUG) Log.d(TAG,"p2p video req sent faile:friend offline");
						for(int i=0;i<mVideoEvntList.size();i++){
							if(mVideoEvntList.get(i)==null){
								continue;
							}
							mVideoEvntList.get(i).onP2PVideoError(
									IMPSDev.getContext().getResources().getString(R.string.friend_offline),detailCode);
						}
						break;
					case 3://friend reject
						if(DEBUG) Log.d(TAG,"p2p video req sent fail:friend reject");
						for(int i=0;i<mVideoEvntList.size();i++){
							if(mVideoEvntList.get(i)==null){
								continue;
							}
							mVideoEvntList.get(i).onP2PVideoError(
									IMPSDev.getContext().getResources().getString(R.string.friend_reject),detailCode);
						}
						break;
					case 4://net error
						if(DEBUG) Log.d(TAG,"p2p video req sent faile:net error");
						for(int i=0;i<mVideoEvntList.size();i++){
							if(mVideoEvntList.get(i)==null){
								continue;
							}
							mVideoEvntList.get(i).onP2PVideoError(
									IMPSDev.getContext().getResources().getString(R.string.net_error),detailCode);
						}
						break;
					case 5://other error
						if(DEBUG) Log.d(TAG,"p2p video req sent faile:unknown error");
						for(int i=0;i<mVideoEvntList.size();i++){
							if(mVideoEvntList.get(i)==null){
								continue;
							}
							mVideoEvntList.get(i).onP2PVideoError(
									IMPSDev.getContext().getResources().getString(R.string.net_error),detailCode);
						}
						break;
					
					}
					break;
				case 8://send path error
					detailCode = inMsg.readInt();
					int sendPathId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						break;
					case 2://send interrupt
						break;
					case 3://net error
						break;
					case 4://other error
						break;
					}
					break;
				case 9://request path error
					detailCode = inMsg.readInt();
					int requestPathId = inMsg.readInt();
					switch(detailCode){
					case 1://not exists
						break;
					case 2://send fail
						break;
					case 3://net error
						break;
					case 4://other error
						break;
					}
					break;
				case 10://refresh status error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1://refresh success
						break;
					case 2://refresh fail
						break;
					}
					break;
				case 11://get friend list error
					detailCode = inMsg.readInt();
					int requestListId = inMsg.readInt();
					switch(detailCode){
					case 1://get friend fail
						if(DEBUG){Log.d(TAG,"get friend fail.");}
						break;
					}
					break;
				case 12://other error
					if(DEBUG){Log.d(TAG,"other error msg recv.");}
					break;
				default://not handle error message 
					if(DEBUG){Log.d(TAG,"not handle error msg recv.");}
					break;
				}
			
			break;
		case CommandId.S_LOGIN_RSP:
			if(DEBUG){Log.d(TAG,"login rsp msg recv:"+mLoginEvntList.size());}
			//TODO:notify user manager
			User user = parseLoginRsp(inMsg);
			user.setPassword(UserManager.getGlobaluser().getPassword());
			UserManager.setGlobaluser(user);
			for(int i=0;i<mLoginEvntList.size();i++){
				mLoginEvntList.get(i).onLoginSuccess();
			}
			break;
		case CommandId.S_HEARTBEAT_RSP:
			if(DEBUG){Log.d(TAG,"heart beat msg recv.");}
			break;
		case CommandId.S_FRIENDLIST_REFURBISH_RSP:
			if(DEBUG){Log.d(TAG,"friendlist refresh msg recv.");}
			
			UserManager.AllFriList = parseFriListRsp(inMsg);
			
			for(int i=0;i<mContactEvntList.size();i++){
				mContactEvntList.get(i).onUpdateFriList();
			}
			break;
		case CommandId.S_ADDFRIEND_REQ:
			if(DEBUG){Log.d(TAG,"add fri req msg recv.");}
			String reqFriName = parseAddFriReq(inMsg);
			for(int i=0;i<mContactEvntList.size();i++){
				if(mContactEvntList.get(i)==null){
					continue;
				}
				mContactEvntList.get(i).onRecvFriendReq(reqFriName);
			}
			ServiceManager.getmSound().playCommonTone();
			break;
		case CommandId.S_ADDFRIEND_RSP:
			if(DEBUG){Log.d(TAG,"add fri rsp msg recv.");}
			String rspFriName = parseAddFriRsp(inMsg);
			boolean rsprel = parseAddFriRspRel(inMsg);
			for(int i=0;i<mContactEvntList.size();i++){
				if(mContactEvntList.get(i)==null){
					continue;
				}
				mContactEvntList.get(i).onRecvFriendRsp(rspFriName, rsprel);
			}
			ServiceManager.getmSound().playCommonTone();
			break;
		case CommandId.S_SEND_MSG:
			if(DEBUG){Log.d(TAG,"send msg recv.");}
			MediaType media = parseSmsMessage(inMsg);
			if(UserManager.UnReadMessages.containsKey(media.getFriend())){
				UserManager.UnReadMessages.get(media.getFriend()).add(media);
			}else{
				List<MediaType> list = new ArrayList<MediaType>();
				list.add(media);
				UserManager.UnReadMessages.put(media.getFriend(), list);
			}
			for(int i=0;i<mSmsEvntList.size();i++){
				if(mSmsEvntList.get(i)==null)
					continue;
				if(DEBUG)Log.d(TAG,"SMS LIST size:"+mSmsEvntList.size());
				mSmsEvntList.get(i).onSmsRecv(media);
			}
			ServiceManager.getmSound().playNewSms();
			break;
		case CommandId.S_REGISTER:
			if(DEBUG){Log.d(TAG,"register msg recv.");}
			for(int i=0;i<mRegEvntList.size();i++){
				if(mRegEvntList.get(i)==null){
					continue;
				}
				mRegEvntList.get(i).onRegSuccess();
			}
			break;
		case CommandId.S_STATUS_NOTIFY:
			if(DEBUG){Log.d(TAG,"status notify msg recv.");}
			User tuser = parseStatusNotify(inMsg);
			for(int i=0;i<UserManager.AllFriList.size();i++){
				if(UserManager.AllFriList.get(i).getUsername().equals(tuser.getUsername())){
					UserManager.AllFriList.get(i).setStatus(tuser.getStatus());
					break;
				}
			}
			for(int i=0;i<mContactEvntList.size();i++){
				if(mContactEvntList.get(i)==null){
					continue;
				}
				mContactEvntList.get(i).onUpdateFriStatus();
			}
			break;
		case CommandId.S_PTP_AUDIO_REQ:
			if(DEBUG){Log.d(TAG,"p2p audio req msg recv.");}
			P2PReqType audiot = parseP2PReq(inMsg);
			for(int i=0;i<mAudioEvntList.size();i++){
				if(mAudioEvntList.get(i)==null){
					continue;
				}
				mAudioEvntList.get(i).onP2PAudioReq(audiot.friName,audiot.ip,audiot.port);
			}
			ServiceManager.getmSound().playRingTone();
			break;
		case CommandId.S_PTP_AUDIO_RSP:
			if(DEBUG){Log.d(TAG,"p2p audio rsp msg recv.");}
			P2PReqType audiop = parseP2PRsp(inMsg);
			for(int i=0;i<mAudioEvntList.size();i++){
				if(mAudioEvntList.get(i)==null){
					continue;
				}
				mAudioEvntList.get(i).onP2PAudioRsp(audiop.friName,audiop.rel,audiop.ip,audiop.port);
			}
			break;
		case CommandId.S_PTP_VIDEO_REQ:
			if(DEBUG){Log.d(TAG,"p2p video req msg recv.");}
			P2PReqType videor = parseP2PReq(inMsg);
			for(int i=0;i<mVideoEvntList.size();i++){
				if(mVideoEvntList.get(i)==null){
					continue;
				}
				mVideoEvntList.get(i).onP2PVideoReq(videor.friName,videor.ip,videor.port);
			}
			ServiceManager.getmSound().playRingTone();
			break;
		case CommandId.S_PTP_VIDEO_RSP:
			if(DEBUG){Log.d(TAG,"p2p video rsp msg recv.");}
			P2PReqType videop = parseP2PRsp(inMsg);
			for(int i=0;i<mVideoEvntList.size();i++){
				if(mVideoEvntList.get(i)==null){
					continue;
				}
				mVideoEvntList.get(i).onP2PVideoRsp(videop.friName,videop.rel,videop.ip,videop.port);
			}
			break;
		case CommandId.S_SEARCH_FRIEND_RSP:
			if(DEBUG){Log.d(TAG,"search friends rsp msg recv.");}
			List <String > userNames = parseSearchFriRsp(inMsg);
			for(int i=0;i<mContactEvntList.size();i++){
				if(mContactEvntList.get(i)==null){
					continue;
				}
				mContactEvntList.get(i).onRecvSearchFriendRsq(userNames);
			}
			break;
		case CommandId.S_SEND_MSG_RSP:
			if(DEBUG) Log.d(TAG,"send sms rsp recv.");
			String res = parseUserName(inMsg);
			int sId = parseSid(inMsg);
			for(int i=0;i<mSmsEvntList.size();i++){
				if(mSmsEvntList.get(i)==null){
					continue;
				}
				mSmsEvntList.get(i).onSmsSendSuccess(res, sId);
			}
			break;
		case CommandId.S_IMAGE_REQ:
			if(DEBUG) Log.d(TAG,"send image req recv.");
			String friName = parseUserName(inMsg);
			int imageId = parseSid(inMsg);
			int rel = parseSid(inMsg);
			byte frame[] = parseFrame(inMsg);
			for(int i=0;i<mSmsEvntList.size();i++){
				if(mSmsEvntList.get(i)==null){
					continue;
				}
				mSmsEvntList.get(i).onImageRecv(friName,imageId,rel==1?true:false,frame);
			}
			break;
		case CommandId.S_AUDIO_REQ:
			if(DEBUG) Log.d(TAG,"send audio req recv.");
			String friname = parseUserName(inMsg);
			int audioId = parseSid(inMsg);
			int eof = parseSid(inMsg);
			byte audioframe[] = parseFrame(inMsg);
			for(int i=0;i<mSmsEvntList.size();i++){
				if(mSmsEvntList.get(i)==null){
					continue;
				}
				mSmsEvntList.get(i).onAudioRecv(friname,audioId,eof==1?true:false,audioframe);
			}
			break;
		default:
			if(DEBUG){Log.d(TAG,"not handle msg recv.CommandId is:"+cmdType);}
			break;
		}
	}
	@Override
	public void addConnEventHandler(IConnEvent event) {
		// TODO Auto-generated method stub
		if(!mConnEvntList.contains(event))
			mConnEvntList.add(event);
	}
	@Override
	public void removeConnEventHandler(IConnEvent event) {
		// TODO Auto-generated method stub
		mConnEvntList.remove(event);
	}
	@Override
	public void addSmsEventHandler(ISmsEvent event) {
		// TODO Auto-generated method stub
		if(!mSmsEvntList.contains(event))
			mSmsEvntList.add(event);
	}
	@Override
	public void removeSmsEventHandler(ISmsEvent event) {
		// TODO Auto-generated method stub
		mSmsEvntList.remove(event);
	}
	@Override
	public void addContactEventHandler(IContactEvent event) {
		// TODO Auto-generated method stub
		if(!mContactEvntList.contains(event))
			mContactEvntList.add(event);
	}
	@Override
	public void removeContactEventHandler(IContactEvent event) {
		// TODO Auto-generated method stub
		mContactEvntList.remove(event);
	}

	//Message Parser
	
	private User parseLoginRsp(ChannelBuffer inMsg){
		User myuser = new User();
		int len = inMsg.readInt();
		byte[] nm = new byte[(int) len];			
		inMsg.readBytes(nm);
		String username = new String(nm);
		if(DEBUG) Log.d(TAG,"username:"+username);

		int gender=inMsg.readInt();

		len = inMsg.readInt();
		byte[] mail = new byte[(int)len];
		inMsg.readBytes(mail);
		String email = new String(mail);
		
		myuser.setEmail(email);
		myuser.setGender(gender);
		myuser.setUsername(username);
		myuser.setStatus(UserStatus.ONLINE);
	
		return myuser;
	}
	
	private List<User> parseFriListRsp(ChannelBuffer inMsg){
		List<User>friendList = new ArrayList<User>();
		int count = inMsg.readInt();
		for(int i=0;i<count;i++)
		{
			//name
			int len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			String username = new String(nm);
			//status
			byte status = inMsg.readByte();
			//email
			len = inMsg.readInt();
			byte[] em = new byte[(int)len];
			inMsg.readBytes(em);
			String email = new String(em);
			//time
		    len = inMsg.readInt();
		    byte[] tm = new byte[(int)len];
		    inMsg.readBytes(tm);
		    String strtm = new String(tm);
		    //location
		    double x = 0,y=0;
		    x = inMsg.readDouble();
		    y = inMsg.readDouble();
		    User user = new User();
		    user.setLoctime(strtm);
		    user.setUsername(username);
		    user.setLocX(x);
		    user.setLocY(y);
		    user.setStatus(status);
		    user.setEmail(email);
		    friendList.add(user);
		}
		return friendList;
	}
	private MediaType parseSmsMessage(ChannelBuffer inMsg){
		MediaType media = new MediaType(MediaType.SMS);
		int len;
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			String friname = new String(nm,"gb2312");
			media.setFriend(friname);
			len = inMsg.readInt();
			byte[] msgcnt = new byte[(int)len];
			inMsg.readBytes(msgcnt);
			String msg = new String(msgcnt,"gb2312");
			media.setMsgContant(msg);
			len = inMsg.readInt();
			byte[] sendtime = new byte[(int)len];
			inMsg.readBytes(sendtime);
			String stime= new String(sendtime,"gb2312");
			media.setTime(stime);
			if(DEBUG) Log.d(TAG,friname+"  "+stime+ "  says to you: "+msg);
		}catch (IOException e){
			e.printStackTrace();
		}
		return media;
	}
	private User parseStatusNotify(ChannelBuffer inMsg){
		User user = new User();
		int len;
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			String friname = new String(nm,"gb2312");
			user.setUsername(friname);
			byte status = inMsg.readByte();
			user.setStatus(status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return user;
	}
	private P2PReqType parseP2PReq(ChannelBuffer inMsg){
		int len;
		P2PReqType rel = new P2PReqType();
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			String friname = new String(nm,"gb2312");
			rel.friName = friname;
			//ip
			len = inMsg.readInt();
			byte[] ipbyte = new byte[(int)len];
			inMsg.readBytes(ipbyte);
			String myip = new String(ipbyte,"gb2312");
			rel.ip = myip;
			//port 
			int port = inMsg.readInt();
			rel.port = port;
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rel;
	}
	private P2PReqType parseP2PRsp(ChannelBuffer inMsg){
		P2PReqType rel = new P2PReqType();
		int len;
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			String friname = new String(nm,"gb2312");
			rel.friName = friname;
			//res
			int res = inMsg.readInt();
			rel.rel = res==0?false:true;
			//ip
			len = inMsg.readInt();
			byte[] ipbyte = new byte[(int)len];
			inMsg.readBytes(ipbyte);
			String ip = new String(ipbyte,"gb2312");
			rel.ip = ip;
			int port = inMsg.readInt();
			rel.port = port;
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rel;
	}
	private class P2PReqType{
		public String friName;
		public String ip;
		public boolean rel;
		public int port;
	}
	private String parseAddFriReq(ChannelBuffer inMsg){
		String friName = "";
		
		int len;
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			//friend name
			friName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return friName;
	}
	private String parseAddFriRsp(ChannelBuffer inMsg){
		String friName = "";
		
		int len;
		try {
			len = inMsg.readInt();
			byte[] nm = new byte[(int)len];
			inMsg.readBytes(nm);
			//friend name
			friName = new String(nm,"gb2312");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return friName;
	}
	private boolean parseAddFriRspRel(ChannelBuffer inMsg){
		boolean rel = false;

		int res = inMsg.readInt();
		if(res==0){
			rel = false;
		}else{
			rel = true;
		}
		return rel;
	}
	private List <String> parseSearchFriRsp(ChannelBuffer inMsg){
		List <String> res = new ArrayList <String>();
		int len;
		try {
			len =0;
			byte[] nm;
			String friname ;
			int sz = inMsg.readInt();
			if(DEBUG) Log.d(TAG,"list size is "+sz);
			for(int i=0;i<sz;i++){
				len = inMsg.readInt();
				nm = new byte[(int)len];
				inMsg.readBytes(nm);
				friname = new String(nm,"gb2312");
				res.add(friname);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	private String parseUserName(ChannelBuffer inMsg){
		String res = "";
		int len = inMsg.readInt();
		byte nm[] = new byte[(int)len];
		inMsg.readBytes(nm);
		try {
			res = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	private int parseSid(ChannelBuffer inMsg){
		
		int res =  inMsg.readInt();

		return res;
	}
	private byte[] parseFrame(ChannelBuffer inMsg){

		int len = inMsg.readInt();
		byte data[] = new byte[(int)len];
		inMsg.readBytes(data);
		
		return data;
	}
	@Override
	public void addRegEventHandler(IRegisterEvent event) {
		mRegEvntList.add(event);
	}
	@Override
	public void removeRegEventHandler(IRegisterEvent event) {
		mRegEvntList.remove(event);
	}
	@Override
	public void addLoginEventHandler(ILoginEvent event) {
		if(DEBUG) Log.d(TAG,"ADD login...");
		mLoginEvntList.add(event);
		
	}
	@Override
	public void removeLoginEventHandler(ILoginEvent event) {
		mLoginEvntList.remove(event);
	}
	@Override
	public void addVideoEventHandler(IVideoEvent event) {
		mVideoEvntList.add(event);
		
	}
	@Override
	public void removeVideoEventHandler(IVideoEvent event) {
		mVideoEvntList.add(event);
		
	}
	@Override
	public void addAudioEventHandler(IAudioEvent event) {
		mAudioEvntList.add(event);
		
	}
	@Override
	public void removeAudioHandler(IAudioEvent event) {
		mAudioEvntList.remove(event);
		
	}
}
