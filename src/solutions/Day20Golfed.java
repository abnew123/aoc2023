import java.util.*;class T{
int R,X,u,v,j,k,z,t[]=new int[677],q[]=new int[512],M[]=t.clone(),o[][]=new int[677][];Map Z=new HashMap();
String s(boolean p,String[]I){
for(var L:I){var a=L.split(" -> |, ");u=a[0].charAt(0);v=u<39?78-u*2:1;z=i(a[0].substring(1-v%2));R=(t[z]=v)<2?z:R;o[z]=new int[a.length-1];for(j=1;j<a.length;){o[z][j-1]=i(a[j]);if(a[j++].equals("rx"))X=z;}}
for(j=677;j-->0;)if(t[j]>0)for(k=o[j].length;k-->0;)if(t[z=o[j][k]]%8==2)o[j][k]|=(t[z]+=8)/8<<10;
if(p){long H=0,L=0;for(j=1000;j-->0;){e();while(u<v){H-=q[u]>>31;L++;d(q[u++]);}}return""+H*(L-H);}int W=t[X]/8,S=0;long A=1;for(k=0;W>0;){k++;e();while(u<v){z=q[u++];if(z<0&(z&1023)==X&(S&1<<(j=z>>10&1023))<1){S|=1<<j;A*=k;W--;}d(z);}}return""+A;
}
int i(String s){return(int)Z.computeIfAbsent(s,k->Z.size());}
void e(){u=v=0;A(0>1,R);}
void d(int z){var H=z<0;int x=z&1023,y=t[x]&7;if(y<1)return;if(y<4)if(y>1){int q=z>>10&1023;M[x]=H?M[x]|1<<q:M[x]&~(1<<q);H=M[x]+2<2<<t[x]/8;}else H=0>1;else{if(H)return;H=(t[x]^=1)>4;}for(int k:o[x])A(H,k);}
void A(boolean H,int T){q[v++]=H?T|1<<31:T;}
}
