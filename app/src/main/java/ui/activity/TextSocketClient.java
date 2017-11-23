package ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.x.xhodgepodgeandroid.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.LogRecord;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/13.
 */

public class TextSocketClient extends BaseActivity {
    @Bind(R.id.about)
    Button about;
Handler handler=new Handler() {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

    }
};



    @Override
    public int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews() {

    }
    @OnClick(R.id.about)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about:
               new Thread(networkTask).start();

                break;
        }
    }
    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
//            Message msg = new Message();
//            Bundle data = new Bundle();
//            data.putString("value", "请求结果");
//            msg.setData(data);
//            handler.sendMessage(msg);
            //客户端
            //1、创建客户端Socket，指定服务器地址和端口
            Socket socket;
            try {
                socket = new Socket("172.18.4.60",10086);
                //2、获取输出流，向服务器端发送信息
                OutputStream os = socket.getOutputStream();//字节输出流
                PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流
                pw.write("用户名：admin；密码：admin");
                pw.flush();
                socket.shutdownOutput();
                //3、获取输入流，并读取服务器端的响应信息
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String info = null;
                while((info=br.readLine())!=null){
                    System.out.println("Hello,我是客户端，服务器说："+info);
                    Log.e("text",info.toString()+"");
                }


                //4、关闭资源
                br.close();
                is.close();
                pw.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                Log.e("text",e.toString());

                e.printStackTrace();
            }

        }
    };

}
