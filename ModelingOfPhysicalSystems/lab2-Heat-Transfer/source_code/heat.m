%Simulation with boundary conditions type 1

close all
clear all
clc

%Initial Parameters
a=0.2;
b=0.05;
dx=0.01;
dy=0.01;
dt=0.1;
Nt=1000;
Nx=a/dx;
Ny=a/dy;
material='Cooper';
K=0;
Cw=0;
Rho=0;

if strcmp(material,'Alumina')
    K=237;
    Cw=900;
    Rho=2700;
elseif strcmp(material,'Cooper')
    K=401;
    Cw=380;
    Rho=8920;
else
    K=58;
    Cw=450;
    Rho=7860;
end;  

T=zeros(Nx,Ny,Nt);
%Boundary Conditions v1
T(:,:,1)=20; %Initial Condition
T(1,:,:)=10;
T(Nx,:,:)=10;
T(:,1,:)=10;
T(:,Ny,:)=10;

T((0.075/dx):(Nx-0.075/dx),(0.075/dy):(Ny-0.075/dy),:)=80;


for t=1:Nt-1
    for i=2:Nx-1
        for j=2:Ny-1
            if ~((i>=(0.075/dx) &&  i<=(Nx-0.075/dx)) && (j>=(0.075/dy) &&  j<=(Nx-0.075/dy) ) )
            T(i,j,t+1)=T(i,j,t)+(K*dt/Cw*Rho*dx*dx)*(T(i+1,j,t)-2*T(i,j,t)+T(i-1,j,t))+(K*dt/Cw*Rho*dy*dy)*(T(i,j+1,t)-2*T(i,j,t)+T(i,j-1,t));
            end;
            
        end;
    end;
end;
[XX YY]=meshgrid(dx:dx:a,dy:dy:a);
figure
for tt=1:t+1
%     subplot(1,2,1)
    val=-1;
    if tt>1
        val=sum(sum(T(:,:,tt)-T(:,:,tt-1)));
    end;
    heatmap(T(:,:,tt));
    title(['time=  ',num2str(tt*dt), ' (s) diff=',num2str(val)]);
%     subplot(1,2,2)
%     surf(XX,YY,T(:,:,t+1));
%     title(['time=  ',num2str(tt*dt), ' (s)']);
    pause(0.1);
    clf;
end;


title(['Simulation time=  ',num2str(t*dt), ' (s)']);
xlabel('x (m)');
ylabel('y (m)');
zlabel('Temperature (C)');

