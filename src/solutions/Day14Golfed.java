import java.util.*;
class N{
char[]g;int r,c,d;
String s(boolean p,String[]l){r=l.length;c=l[0].length();g=String.join("",l).toCharArray();if(p)t(0);else{var m=new HashMap();for(int i=0;i<1e9;i++){q();var q=(Integer)m.put(new String(g),i);if(q!=null){for(int j=(999999999-i)%(i-q);j-->0;)q();break;}}}return w()+"";}
void q(){for(d=0;d<4;)t(d++);}
void t(int d){int a,b,k,x,l,z=d>1?-1:1;var h=d%2>0;l=h?c:r;for(a=h?r:c;a-->0;){k=d>1?l-1:0;for(b=k;b>=0&b<l;b+=z){x=h?a*c+b:b*c+a;if(g[x]==35)k=b+z;else if(g[x]==79){g[x]=46;g[h?a*c+k:k*c+a]=79;k+=z;}}}}
int w(){int a=0,i=g.length;while(i-->0)if(g[i]==79)a+=r-i/c;return a;}
}
