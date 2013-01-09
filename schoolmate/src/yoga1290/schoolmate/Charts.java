package yoga1290.schoolmate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class Charts 
{
	public static int gcd(int a,int b)
	{
		if(b==0)
			return a;
		return gcd(b,a%b);
	}
	public static Bitmap getTimepeice(int width,int height,int ar[],Paint paint)
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas=new Canvas(bitmap);
		int i,l=Math.min(ar.length, 12),angle=0,sum=0,gcd=ar[0],lpad,tpad;
		for(i=0;i<ar.length;i++)
			gcd=gcd(ar[i],gcd);
		for(i=0;i<ar.length;i++)
			ar[i]=ar[i]/gcd;
		for(i=0;i<ar.length;i++)
			sum+=ar[i];
		for(i=0;i<l;i++)
		{
			lpad=ar[i]/sum*Math.min(width/2, height/2);
			tpad=Math.min(width/2, height/2)-lpad;
			canvas.drawArc(new RectF(lpad,lpad, tpad, tpad), angle, 30, true, paint);
			angle+=30;
		}
		return bitmap;
	}
}
