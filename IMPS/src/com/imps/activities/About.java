package com.imps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.imps.R;

public class About extends Activity {

	private TextView aboutView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		aboutView = (TextView)findViewById(R.id.about_content);
		aboutView.setText(
				"				����IMPS			\n" +
				"	IMPS��һ���������������Ӧ������ܽ����һ��Ļ��ڵ���λ�õ�ʵʱ����ͨ��Ӧ�á�" +
				"IMPS�û��ܹ�����λ�÷�����л���CSģʽ�İ������֡�ͼƬ�������Խ������ڵ�����ʵʱ��" +
				"���ͻ���P2Pģʽ�Ķ�ý��ͨѶ��������Ƶ����������ƵͨѶ�ȣ������ܹ�ʹ����д��Ϳѻ��" +
				"�������ӻ�����Ȥζ�ԡ����⻹�����û������ƶ��豸�ϵĶ��ֶ�λ�ֶ�����ȡ���ḻ��λ��" +
				"��Ϣ����\n" +
				"	IMPS������һ�����ڶ�Э�飨����tcp,udp,rtp�ȣ��Ķ�ý�崫������ܹ������ܹ���Ϊƽ̨��" +
				"��Ϊ��ͬ�����ṩ��ý�崫���봦�������ý�崦����ʵ������PCMU�������Ƶ����H264��H263" +
				"ѹ���ʺܸߡ��ڹ�ҵ��õ��㷺�Ͽɵ��㷨�������Ƶ�Ĺ��ܣ�ͬʱ�ڷ���ͨ�Ų��������˺ܴ���Ż���\n" +
				"	IMPS��Ҫ���������ص㣺\n" +
				"	1. ������һ�����ڶ�Э�飨����tcp,udp,rtp�ȣ��Ķ�ý�崫������ܹ������ܹ���Ϊƽ̨����Ϊ" +
				"��ͬ�����ṩ��ý�崫����ѹ���������\n" +
				"   2. ʵ����һ����չ��NIO����ͨ�ſ�ܣ���ͨ�Ų��������˽϶��Ż���\n" +
				"	3. �߱������ֻ��豸�Ķ�������Ӧ��λ��GPS����վ��λ��wifi��λ�����ܡ�\n" +
				"	4. �߱������ֻ�����λ�õİ������֡�ͼƬ����������Ƶ�����ڵļ�ʱͨ�Ź��ܣ��ں�����������������" +
				"Ԫ�أ�ʵ��Ȥζ�Ի�����\n" +
				"	IMPS��Ҫ�����ĺ���ɣ�ͬʱ������������嫡���ӽ������Ŀ�и����˴�����֧��\n" +
				"	IMPSĿǰ�Դ�����Ҫ�з��׶Σ�����������չ���������������ݣ��й�����ɷ��ʼ���liwenhaosuper@126.com" +
				"�������߽���\n\n"
				);		
		//aboutView.setTextColor(color.darker_gray);
	}

}
