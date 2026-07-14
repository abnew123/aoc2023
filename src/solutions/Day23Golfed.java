class W{
int[]D;int[][]g;String G;int L,e;
String s(boolean p,String[]Z){G=String.join("\n",Z);int w=G.indexOf(10)+1,N=0,h[]=new int[L=G.length()],S=G.indexOf(46),E=G.lastIndexOf(46);D=new int[]{1,w,-1,-w};
for(int v=L;v-->0;)if(o(v)&(v==S|v==E|n(v)!=2))h[v]=++N;
g=new int[N][9];e=h[E]-1;
for(int v=L;v-->0;)if(h[v]>0)for(int d=4;d-->0;)if(a(G.charAt(v),p,d))A(h,h[v]-1,v,d,p);
int z=h[S]-1;return""+d(z,1L<<z);
}
void A(int[]h,int f,int v,int d,boolean p){
int s=0;for(;;){v+=D[d];s++;if(!o(v))return;int t=h[v];if(t>0){int[]a=g[f];a[++a[0]*2-1]=t-1;a[a[0]*2]=s;return;}int q=-1;for(int n=4;n-->0;)if(a(G.charAt(v),p,n)&o(v+D[n])&n!=(d^2))q=n;if(q<0)return;d=q;}
}
int d(int a,long s){if(a==e)return 0;int b=-1<<20;for(int i=1;i<g[a][0]*2;i+=2){int x=g[a][i];long y=1L<<x;if((s&y)==0)b=Math.max(b,g[a][i+1]+d(x,s|y));}return b;}
boolean a(char c,boolean p,int d){return!p|c<47|c==">v<^".charAt(d);}
int n(int v){int n=0;for(int d:D)if(o(v+d))n++;return n;}
boolean o(int v){return v>=0&v<L&&G.charAt(v)>35;}
}
