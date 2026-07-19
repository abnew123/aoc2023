class W{
int D[],g[][],L,e;String G;boolean P;
String s(boolean p,String[]Z){P=p;G=String.join("\n",Z);int w=G.indexOf(10)+1,N=0,h[]=new int[L=G.length()],S=G.indexOf(46),E=G.lastIndexOf(46);D=new int[]{1,w,-1,-w};
for(int v=L,n;v-->0;)if(o(v)){n=0;for(int d:D)if(o(v+d))n++;if(v==S|v==E|n!=2)h[v]=++N;}
g=new int[N][N];e=h[E]-1;
for(int v=L,n;v-->0;)if(h[v]>0)for(int d=4;d-->0;)if(a(G.charAt(v),d))for(int f=h[v]-1,x=v,t=d,s=0;;){if(!o(x+=D[t]))break;s++;if(h[x]>0){n=h[x]-1;if(s>g[f][n])g[f][n]=s;break;}n=4;while(n-->0&&!(a(G.charAt(x),n)&o(x+D[n])&n!=(t^2)));if(n<0)break;t=n;}
return""+d(h[S]-1);
}
int d(int a){if(a==e)return 0;g[a][a]+=L;int b=-L;for(int z=g.length;z-->0;)if(g[a][z]>0&g[z][z]<L)b=Math.max(b,g[a][z]+d(z));g[a][a]-=L;return b;}
boolean a(char c,int d){return!P|c<47|c==">v<^".charAt(d);}
boolean o(int v){return v>=0&v<L&&G.charAt(v)>35;}
}
