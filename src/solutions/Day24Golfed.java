class X{
 long[][]h;int n;
 String s(boolean p,String[]I){h=new long[I.length][6];for(var L:I){var a=L.split("[, @]+");for(int i=0;i<6;)h[n][i]=new Long(a[i++]);n++;}return""+(p?x():r());}
 long x(){long A=0;for(int i=n;i-->0;)for(int j=i;j-->0;){var a=h[i];var b=h[j];long d=a[3]*b[4]-a[4]*b[3],q=(b[0]-a[0])*b[4]-(b[1]-a[1])*b[3],Q=(a[0]-b[0])*a[4]-(a[1]-b[1])*a[3];if(d!=0&&q/d>0&&Q/d<0){double X=q/(double)d*a[3]+a[0],Y=q/(double)d*a[4]+a[1];if(X>=2e14&X<=4e14&Y>=2e14&Y<=4e14)A++;}}return A;}
 long r(){var q=new double[3*n-3][7];for(int j=n;j-->1;){var a=h[0];var b=h[j];for(int k=3;k-->0;){int d=(k+1)%3,e=(k+2)%3,i=3*j-3+k;var z=q[i];z[d]=b[e+3]-a[e+3];z[e]=a[d+3]-b[d+3];z[d+3]=a[e]-b[e];z[e+3]=b[d]-a[d];z[6]=b[d]*b[e+3]-b[e]*b[d+3]-a[d]*a[e+3]+a[e]*a[d+3];}}if(!g(q))return 0;var R=new long[6];for(int i=6;i-->0;)R[i]=Math.round(q[i][6]);for(var a:h){int d=0;while(d<3&&R[d+3]==a[d+3])d++;if(d>2&(R[0]!=a[0]|R[1]!=a[1]|R[2]!=a[2]))return 0;d%=3;long D=R[d+3]-a[d+3],N=a[d]-R[d];if(N*D<0)return 0;for(int k=3;k-->0;)if((R[k+3]-a[k+3])*N!=(a[k]-R[k])*D)return 0;}return R[0]+R[1]+R[2];}
 boolean g(double[][]a){for(int i=0;i<6;i++){int p=i;for(int j=a.length;j-->i;)if(Math.abs(a[j][i])>Math.abs(a[p][i]))p=j;if(a[p][i]==0)return 1<0;var q=a[p];a[p]=a[i];a[i]=q;for(int k=7;k-->i;)q[k]/=q[i];for(int j=a.length;j-->0;)if(j!=i)for(int k=7;k-->i;)a[j][k]-=a[j][i]*q[k];}return 1>0;}
}
