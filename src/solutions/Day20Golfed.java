class T{
int R,X,u,v,t[]=new int[677],q[]=new int[512],M[]=new int[677],o[][]=new int[677][];java.util.Map Z=new java.util.HashMap();
String s(boolean P,String[]I){
for(var L:I){var a=L.split(" -> ");var w=a[1].split(", ");int c=a[0].charAt(0),y=c<39?78-c*2:1,n=i(a[0].substring(y<2?0:1));t[n]=y;if(y<2)R=n;var r=new int[w.length];for(int j=0;j<w.length;){r[j]=i(w[j]);if(w[j++].equals("rx"))X=n;}o[n]=r;}
for(int j=0;j<677;j++)if(o[j]!=null)for(int k=0;k<o[j].length;k++)if(t[o[j][k]]%8==2)o[j][k]|=(t[o[j][k]]+=8)/8<<10;
return""+(P?P():C());
}
int i(String s){return(int)Z.computeIfAbsent(s,k->Z.size());}
long P(){long H=0,L=0;for(int j=1000;j-->0;){e();while(u<v){if(q[u]<0)H++;else L++;d(q[u++]);}}return H*L;}
long C(){int W=t[X]/8,s[]=new int[W+1];for(int P=1,F=0;P<10000&F<W;P++){e();while(u<v){int j=q[u]>>10&1023;if(q[u]<0&&(q[u]&1023)==X&&s[j]<1){s[j]=P;F++;}d(q[u++]);}}long A=1;for(int j=1;j<=W;)A*=s[j++];return A;}
void e(){u=v=0;A(0>1,R);}
void d(int z){var H=z<0;int x=z&1023,y=t[x];if(y<1)return;y&=7;if(y<4){if(y>1){int q=z>>10&1023;if(H)M[x]|=1<<q;else M[x]&=~(1<<q);H=M[x]<(2<<t[x]/8)-2;}else H=0>1;}else{if(H)return;H=(t[x]^=1)>4;}for(int k:o[x])A(H,k);}
void A(boolean H,int T){q[v++]=T|(H?1<<31:0);}
}
