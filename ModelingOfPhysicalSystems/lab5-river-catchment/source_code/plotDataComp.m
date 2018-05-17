input = importdata('opady.prn');
output = importdata('dunaj.prn');

in_count = size(input, 1);
% calculated optimal tt
tt = 7.24;
integralResult = zeros(in_count, 1);

% we don't have output data for 0 .. 161
for i = 162:in_count
    integralResult(i) = convInt(input, i, tt);
end

figure;
plot(output(:,2));
hold on;
plot(integralResult);
xlabel('Time in months');
ylabel('Tritium tracer concentration');
legend('Measured output.', 'Calculated output.');