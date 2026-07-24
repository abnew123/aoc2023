class W{
int g[],L;byte[]G;
String s(boolean p,String[]Z){G=String.join("",Z).getBytes();int N=0,h[]=new int[L=G.length],w=L/Z.length,v,D[]={-1,-w,1,w};
for(v=L;v-->0;)if(o(v)){w=0;for(int d:D)if(o(v+d))w++;h[v]=w!=2?N+=5:0;}
g=new int[L];
for(v=L;v-->0;)if(h[v]>0)for(int e=4;e-->0;)for(int x=v,t=e,s=0;(!p|G[x]<47|G[x]%11==t+5)&o(x+=D[t])&&(h[x]<1||(g[h[v]-~e]=h[x]*L-~s)<0);t=w,s++)for(w=4;!o(x+D[--w])|w==(t^2););
return""+d(N);
}
int d(int a){if(a<6)return 0;int b=-L;for(int z=g[a]=5,q;z-->1;)if((q=g[a+z])>0&g[q/L]<1&&(q=q%L+d(q/L))>b)b=q;g[a]=0;return b;}
boolean o(int v){return v>0&v<L&&G[v]>35;}
}
