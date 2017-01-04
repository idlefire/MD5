import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Javamd5 {
	// 定义CLS左移次数
	static int S11 = 7, S12 = 12, S13 = 17, S14 = 22, S21 = 5, S22 = 9, S23 = 14, S24 = 20, S31 = 4, S32 = 11, S33 = 16,
			S34 = 23, S41 = 6, S42 = 10, S43 = 15, S44 = 21;
	//填充的数组
	static byte[] PAD = new byte[64];
	/*
	 * buffer数组用于存储剪切的512位预处理报文，digest为新一次128bit的MD5值计算结果，state数组存放ABCD报文缓存，并且设置ABCD初始值
	 * count存报文长度(2进制值)
	 */
	byte[] buffer = new byte[64], digest = new byte[16];
	static long[] count = { 0L, 0L }, state = { 0x67452301L, 0xefcdab89L, 0x98badcfeL, 0x10325476L };

	public String md5encode(String input) {
		ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
		PrintStream cache =new PrintStream(baoStream);
		PrintStream oldStream = System.out;
		System.setOut(cache);
		//Scanner sc=new Scanner(System.in);
		PAD[0] = -128;
		for (int i = 1; i < 64; i++)
			PAD[i] = 0;
		Javamd5 app = new Javamd5();
		//String input = sc.nextLine(); // 输入明文
		String output = app.tomd5(input); // 生成密文
		String message = baoStream.toString();
		System.setOut(oldStream);
		//System.out.println("'"+input+"'的加密结果为:"+output); // 输出报文
		//System.out.println(message);
		return output+"~"+message;
	}

	public String tomd5(String inbuf) {
		outfirst(inbuf, inbuf.length());
		md5up(inbuf.getBytes(), inbuf.length());
		md5f();
		String estr = "";
		for (int i = 0; i < 16; i++)
			estr += bytetohex(digest[i]);
		return estr;
	}

	/**
	 * 四个非线性函数
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	long F(long x, long y, long z) {
		return (x & y) | ((~x) & z);
	}

	long G(long x, long y, long z) {
		return (x & z) | (y & (~z));
	}

	long H(long x, long y, long z) {
		return (x ^ y ^ z);
	}

	long I(long x, long y, long z) {
		return (y ^ (x | (~z)));
	}

	/**
	 * t 表示T函数的随机变量,使用左移与循环右移达到循环左移的效果
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param x
	 * @param s
	 * @param t
	 * @return
	 */
	long X1(long a, long b, long c, long d, long x, long s, long t) {
		a += F(b, c, d) + x + t;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		return (a += b);
	}

	long X2(long a, long b, long c, long d, long x, long s, long t) {
		a += G(b, c, d) + x + t;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		return (a += b);
	}

	long X3(long a, long b, long c, long d, long x, long s, long t) {
		a += H(b, c, d) + x + t;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		return (a += b);
	}

	long X4(long a, long b, long c, long d, long x, long s, long t) {
		a += I(b, c, d) + x + t;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		return (a += b);
	}

	/**
	 * 处理填充的函数
	 * @param inbuf
	 * @param inputLen
	 */
	void md5up(byte[] inbuf, int inputLen) {
		int i, index, partLen;
		byte[] block = new byte[64];
		index = (int) (count[0] >>> 3) & 0x3F;
		if ((count[0] += (inputLen << 3)) < (inputLen << 3))
			count[1]++;
		count[1] += (inputLen >>> 29);
		partLen = 64 - index;
		if (inputLen >= partLen) {
			md5m(buffer, inbuf, index, 0, partLen);
			trans(buffer);
			for (i = partLen; i + 63 < inputLen; i += 64) {
				md5m(block, inbuf, 0, i, 64);
				trans(block);
			}
			index = 0;
		} else
			i = 0;
		md5m(buffer, inbuf, index, i, inputLen - i);
	}

	/**
	 * 处理最后一块的数据
	 */
	void md5f() {
		byte[] bits = new byte[8];
		int index, padLen;
		Encode(bits, count, 8);
		index = (int) (count[0] >>> 3) & 0x3f;
		// 判断index<56?
		padLen = (index < 56) ? (56 - index) : (120 - index);
		md5up(PAD, padLen);
		md5up(bits, 8);
		Encode(digest, state, 16);
	}

	/**
	 * 将输入的len长的byte[]转存到output中首地址为outpos的byte[]中,达到分块的目的
	 * @param output
	 * @param input
	 * @param outpos
	 * @param inpos
	 * @param len
	 */
	void md5m(byte[] output, byte[] input, int outpos, int inpos, int len) {
		int i;
		for (i = 0; i < len; i++){
			output[outpos + i] = input[inpos + i];
		}
	}

	/**
	 * MD5压缩函数的实现
	 * @param block
	 */
	void trans(byte block[]) {
		long a = state[0], b = state[1], c = state[2], d = state[3];
		long[] x = new long[16];
		System.out.println("填充后的数据:");
		if(block.length==64)
		{
			for(int i =0;i<64;i++){
				if(i%4==0){
					System.out.print("X["+i/4+"]=");
				}
				System.out.print(bytetohex(block[i]));
				if((i+1)%4==0){
					System.out.println();
				}
			}
		}
		Decode(x, block, 64);
		//第一轮加密,a,b,c,d分别改变四次
		a = X1(a, b, c, d, x[0], S11, 0xd76aa478L);// 1
		System.out.println();
		System.out.println("第一轮开始");
		System.out.println("(1)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X1(d, a, b, c, x[1], S12, 0xe8c7b756L);
		System.out.println("(2)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X1(c, d, a, b, x[2], S13, 0x242070dbL);
		System.out.println("(3)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X1(b, c, d, a, x[3], S14, 0xc1bdceeeL);
		System.out.println("(4)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X1(a, b, c, d, x[4], S11, 0xf57c0fafL);
		System.out.println("(5)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X1(d, a, b, c, x[5], S12, 0x4787c62aL);
		System.out.println("(6)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X1(c, d, a, b, x[6], S13, 0xa8304613L);
		System.out.println("(7)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X1(b, c, d, a, x[7], S14, 0xfd469501L);
		System.out.println("(8)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X1(a, b, c, d, x[8], S11, 0x698098d8L);
		System.out.println("(9)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X1(d, a, b, c, x[9], S12, 0x8b44f7afL);// 10
		System.out.println("(10)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X1(c, d, a, b, x[10], S13, 0xffff5bb1L);
		System.out.println("(11)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X1(b, c, d, a, x[11], S14, 0x895cd7beL);
		System.out.println("(12)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X1(a, b, c, d, x[12], S11, 0x6b901122L);
		System.out.println("(13)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X1(d, a, b, c, x[13], S12, 0xfd987193L);
		System.out.println("(14)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X1(c, d, a, b, x[14], S13, 0xa679438eL);
		System.out.println("(15)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X1(b, c, d, a, x[15], S14, 0x49b40821L);
		System.out.println("(16)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		System.out.println("第一轮结束");
		lune(a, 'A');
		lune(b, 'B');
		lune(c, 'C');
		lune(d, 'D');
		System.out.println();
		//第二轮加密,a,b,c,d分别改变四次
		a = X2(a, b, c, d, x[1], S21, 0xf61e2562L);
		System.out.println("第二轮开始");
		System.out.println("(17)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X2(d, a, b, c, x[6], S22, 0xc040b340L);
		System.out.println("(18)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X2(c, d, a, b, x[11], S23, 0x265e5a51L);
		System.out.println("(19)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X2(b, c, d, a, x[0], S24, 0xe9b6c7aaL);// 20
		System.out.println("(20)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X2(a, b, c, d, x[5], S21, 0xd62f105dL);
		System.out.println("(21)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X2(d, a, b, c, x[10], S22, 0x02441453L);
		System.out.println("(22)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X2(c, d, a, b, x[15], S23, 0xd8a1e681L);
		System.out.println("(23)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X2(b, c, d, a, x[4], S24, 0xe7d3fbc8L);
		System.out.println("(24)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X2(a, b, c, d, x[9], S21, 0x21e1cde6L);
		System.out.println("(25)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X2(d, a, b, c, x[14], S22, 0xc33707d6L);
		System.out.println("(26)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X2(c, d, a, b, x[3], S23, 0xf4d50d87L);
		System.out.println("(27)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X2(b, c, d, a, x[8], S24, 0x455a14edL);
		System.out.println("(28)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X2(a, b, c, d, x[13], S21, 0xa9e3e905L);
		System.out.println("(29)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X2(d, a, b, c, x[2], S22, 0xfcefa3f8L);// 30
		System.out.println("(30)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X2(c, d, a, b, x[7], S23, 0x676f02d9L);
		System.out.println("(31)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X2(b, c, d, a, x[12], S24, 0x8d2a4c8aL);
		System.out.println("(32)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		System.out.println("第二轮结束");
		lune(a, 'A');
		lune(b, 'B');
		lune(c, 'C');
		lune(d, 'D');
		System.out.println();
		//第三轮加密,a,b,c,d分别改变四次
		a = X3(a, b, c, d, x[5], S31, 0xfffa3942L);
		System.out.println("第三轮开始");
		System.out.println("(33)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X3(d, a, b, c, x[8], S32, 0x8771f681L);
		System.out.println("(34)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X3(c, d, a, b, x[11], S33, 0x6d9d6122L);
		System.out.println("(35)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X3(b, c, d, a, x[14], S34, 0xfde5380cL);
		System.out.println("(36)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X3(a, b, c, d, x[1], S31, 0xa4beea44L);
		System.out.println("(37)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X3(d, a, b, c, x[4], S32, 0x4bdecfa9L);
		System.out.println("(38)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X3(c, d, a, b, x[7], S33, 0xf6bb4b60L);
		System.out.println("(39)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X3(b, c, d, a, x[10], S34, 0xbebfbc70L);// 40
		System.out.println("(40)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X3(a, b, c, d, x[13], S31, 0x289b7ec6L);
		System.out.println("(41)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X3(d, a, b, c, x[0], S32, 0xeaa127faL);
		System.out.println("(42)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X3(c, d, a, b, x[3], S33, 0xd4ef3085L);
		System.out.println("(43)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X3(b, c, d, a, x[6], S34, 0x04881d05L);
		System.out.println("(44)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X3(a, b, c, d, x[9], S31, 0xd9d4d039L);
		System.out.println("(45)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X3(d, a, b, c, x[12], S32, 0xe6db99e5L);
		System.out.println("(46)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X3(c, d, a, b, x[15], S33, 0x1fa27cf8L);
		System.out.println("(47)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X3(b, c, d, a, x[2], S34, 0xc4ac5665L);
		System.out.println("(48)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		System.out.println("第三轮结束");
		lune(a, 'A');
		lune(b, 'B');
		lune(c, 'C');
		lune(d, 'D');
		System.out.println();
		//第四轮加密,a,b,c,d分别改变四次
		a = X4(a, b, c, d, x[0], S41, 0xf4292244L);
		System.out.println("第四轮开始");
		System.out.println("(49)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X4(d, a, b, c, x[7], S42, 0x432aff97L);// 50
		System.out.println("(50)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X4(c, d, a, b, x[14], S43, 0xab9423a7L);
		System.out.println("(51)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X4(b, c, d, a, x[5], S44, 0xfc93a039L);
		System.out.println("(52)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X4(a, b, c, d, x[12], S41, 0x655b59c3L);
		System.out.println("(53)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X4(d, a, b, c, x[3], S42, 0x8f0ccc92L);
		System.out.println("(54)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X4(c, d, a, b, x[10], S43, 0xffeff47dL);
		System.out.println("(55)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X4(b, c, d, a, x[1], S44, 0x85845dd1L);
		System.out.println("(56)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X4(a, b, c, d, x[8], S41, 0x6fa87e4fL);
		System.out.println("(57)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X4(d, a, b, c, x[15], S42, 0xfe2ce6e0L);
		System.out.println("(58)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X4(c, d, a, b, x[6], S43, 0xa3014314L);
		System.out.println("(59)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X4(b, c, d, a, x[13], S44, 0x4e0811a1L);// 60
		System.out.println("(60)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		a = X4(a, b, c, d, x[4], S41, 0xf7537e82L);
		System.out.println("(61)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		d = X4(d, a, b, c, x[11], S42, 0xbd3af235L);
		System.out.println("(62)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		c = X4(c, d, a, b, x[2], S43, 0x2ad7d2bbL);
		System.out.println("(63)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		b = X4(b, c, d, a, x[9], S44, 0xeb86d391L);
		System.out.println("(64)");
		lune(a, 'A');lune(b, 'B');lune(c, 'C');lune(d, 'D');System.out.println();

		System.out.println("第四轮结束");
		lune(a, 'A');
		lune(b, 'B');
		lune(c, 'C');
		lune(d, 'D');
		System.out.println();

		state[0] += a;
		state[1] += b;
		state[2] += c;
		state[3] += d;
		System.out.println("ABCD最后的值:");
		lune(state[0], 'A');
		lune(state[1], 'B');
		lune(state[2], 'C');
		lune(state[3], 'D');
	}

	/**
	 * 将输入的long[]移位后转存到byte[]中
	 * @param output
	 * @param input
	 * @param len
	 */
	void Encode(byte[] output, long[] input, int len) {
		int i, j;
		for (i = 0, j = 0; j < len; i++, j += 4) {
			output[j] = (byte) (input[i] & 0xffL);
			output[j + 1] = (byte) ((input[i] >>> 8) & 0xffL);
			output[j + 2] = (byte) ((input[i] >>> 16) & 0xffL);
			output[j + 3] = (byte) ((input[i] >>> 24) & 0xffL);
		}
	}

	/**
	 * 将512bit的byte[]转换成long[],即[0]~[15]的分组
	 * @param output
	 * @param input
	 * @param len
	 */
	void Decode(long[] output, byte[] input, int len) {
		int i, j;
		for (i = 0, j = 0; j < len; i++, j += 4)
			output[i] = liu(input[j]) | (liu(input[j + 1]) << 8) | (liu(input[j + 2]) << 16) | (liu(input[j + 3]) << 24);
	}

	/**
	 * 取b的绝对值
	 * @param b
	 * @return
	 */
	static long liu(byte b) {
		return b < 0 ? b & 0x7F + 128 : b;
	}

	/**
	 * byte型数据转换成十六进制
	 * @param ib
	 * @return
	 */
	static String bytetohex(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		return (new String(ob));
	}

	/**
	 * a,b,c,d一轮加密结束的结果(是正确的顺序)
	 * @param a
	 * @param lun
	 * @param A
	 */
	void lune(long a,char A)
	{
		byte[] lune = new byte[4];
		lunencode(lune, a);
		String lunestr ="";
		for (int i =0;i<4;i++)
			lunestr+=bytetohex(lune[i]);
		System.out.print(A+"=0x"+lunestr+'	');
	}

	/**
	 * 轮加密中a,b,c,d转换为byte[]
	 * @param output
	 * @param input
	 */
	void lunencode(byte[] output,long input)
	{
		int i=0;
		output[i] = (byte) (input & 0xffL);
		output[i + 1] = (byte) ((input >>> 8) & 0xffL);
		output[i + 2] = (byte) ((input >>> 16) & 0xffL);
		output[i + 3] = (byte) ((input >>> 24) & 0xffL);
	}

	void outfirst(String input,int length){
		byte[] inputByte = input.getBytes();
		int len = length;
		System.out.println("填充前的数据:");
		for(int i=0;i<len;i++){
			System.out.print(bytetohex(inputByte[i]));
		}
		System.out.println();
	}
}
