class W{
int D[],g[][];String G;int L,e;boolean P;int V[];
String s(boolean p,String[]Z){P=p;G=String.join("\n",Z);int w=G.indexOf(10)+1,N=0,h[]=new int[L=G.length()],S=G.indexOf(46),E=G.lastIndexOf(46);D=new int[]{1,w,-1,-w};
for(int v=L;v-->0;)if(o(v)&(v==S|v==E|n(v)!=2))h[v]=++N;
g=new int[N][N];V=new int[N];e=h[E]-1;
for(int v=L;v-->0;)if(h[v]>0)for(int d=4;d-->0;)if(a(G.charAt(v),d))A(h,h[v]-1,v,d);
return""+d(h[S]-1);
}
void A(int[]h,int f,int v,int d){
int s=0;for(;;){if(!o(v+=D[d]))return;s++;if(h[v]>0){int n=h[v]-1;if(s>g[f][n])g[f][n]=s;return;}int n=4;for(;n-->0;)if(a(G.charAt(v),n)&o(v+D[n])&n!=(d^2))break;if(n<0)return;d=n;}
}
int d(int a){if(a==e)return 0;V[a]=1;int b=-L;for(int z=g.length;z-->0;)if(g[a][z]>0&V[z]<1)b=Math.max(b,g[a][z]+d(z));V[a]=0;return b;}
boolean a(char c,int d){return!P|c<47|c==">v<^".charAt(d);}
int n(int v){int n=0;for(int d:D)if(o(v+d))n++;return n;}
boolean o(int v){return v>=0&v<L&&G.charAt(v)>35;}
}
