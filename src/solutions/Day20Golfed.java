class T{
int R,X,u,v,t[]=new int[677],q[]=new int[512],i[]=new int[512],M[]=new int[677],F[]=new int[677];int[][]o=new int[677][],p=new int[677][9];boolean[]f=new boolean[677],h=new boolean[512];
String s(boolean P,String[]I){
java.util.Arrays.fill(t,-1);
for(var L:I){var a=L.split(" -> ");var w=a[1].split(", ");int c=a[0].charAt(0),y=c==37?1:c==38?2:0,n=i(a[0].substring(y<1?0:1));t[n]=y;if(y<1)R=n;var r=new int[w.length];for(int j=0;j<w.length;){r[j]=i(w[j]);if(w[j++].equals("rx"))X=n;}o[n]=r;}
for(int j=0;j<677;j++)if(o[j]!=null)for(int k:o[j])if(t[k]>1)p[k][++p[k][0]]=j;
for(int j=0;j<677;j++)F[j]=(2<<p[j][0])-2;
return""+(P?P():C());
}
int i(String s){return s.length()>2?0:(s.charAt(0)-96)*26+s.charAt(1)-96;}
long P(){long H=0,L=0;for(int j=1000;j-->0;){e();while(u<v){if(h[u])H++;else L++;d(q[u],i[u],h[u++]);}}return H*L;}
long C(){int W=p[X][0],s[]=new int[W+1];for(int P=1,F=0;P<10000&F<W;P++){e();while(u<v){int j=W;for(;j>0&p[X][j]!=i[u];j--);if(h[u]&j>0&s[j]<1){s[j]=P;F++;}d(q[u],i[u],h[u++]);}}long A=1;for(int j=1;j<=W;)A*=s[j++];return A;}
void e(){u=v=0;A(0>1,R,0);}
void d(int x,int n,boolean H){int y=t[x];if(y<0)return;if(y>1){int z=p[x][0];for(;p[x][z]!=n;z--);if(H)M[x]|=1<<z;else M[x]&=~(1<<z);H=M[x]<F[x];}else if(y>0){if(H)return;H=f[x]=!f[x];}else H=0>1;for(int k:o[x])A(H,k,x);}
void A(boolean H,int T,int I){h[v]=H;q[v]=T;i[v++]=I;}
}
