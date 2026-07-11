import java.util.*;
class N{
char[][]g;int r,c;
String s(boolean p,String[]l){r=l.length;c=l[0].length();g=new char[r][];for(int i=0;i<r;i++)g[i]=l[i].toCharArray();if(p)t(0);else{var m=new HashMap<String,Integer>();for(int i=0;i<1000000000;i++){for(int d=0;d<4;)t(d++);var q=m.put(Arrays.deepToString(g),i);if(q!=null){for(int j=(999999999-i)%(i-q);j-->0;)for(int d=0;d<4;)t(d++);break;}}}return w()+"";}
void t(int d){int a,b,k,x,y,z=d>1?-1:1;boolean h=d%2>0;for(a=h?r:c;a-->0;){k=d>1?(h?c:r)-1:0;for(b=k;b>=0&&b<(h?c:r);b+=z){x=h?a:b;y=h?b:a;if(g[x][y]==35)k=b+z;else if(g[x][y]==79){g[x][y]=46;if(h)g[a][k]=79;else g[k][a]=79;k+=z;}}}}
int w(){int a=0,i,j;for(i=0;i<r;i++)for(j=0;j<c;j++)if(g[i][j]==79)a+=r-i;return a;}
}
