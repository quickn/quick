package cn.quickj.extui.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class VerifycodeAction extends ExtBaseAction {
	public void index() {
		// 在内存中创建图片
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设置背景颜色
		g.setColor(new Color(160, 200, 100));
		g.fillRect(0, 0, width, height);
		// 设置字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		// 随机产生50条干扰线，使图形中的验证码不易被其他的程序探测到
		g.setColor(new Color(160, 200, 200));
		for (int i = 0; i < 5; i++) {
			int x = 0;
			int y = random.nextInt(height);
			int x1 = width;
			int y1 = random.nextInt(height);
			g.drawLine(x, y, x + x1, y + y1);
		}
		// 随机产生验证码（4为数字）
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
			// 将验证码显示到图象
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 13 * i + 6, 16);
		}
		setAttribute("verifyCode", sRand); // ////将产生的验证码存储到sesson中
		g.dispose();
		getResponse().setHeader("Pragma", "No-cache");
		getResponse().setHeader("Cahce-Control", "no-cache");
		getResponse().setDateHeader("Expires", 0);
		try {
			ImageIO.write(image, "JPEG", getResponse().getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
