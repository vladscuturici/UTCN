library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity neuron_16inputs is
    port(
        inputs : in std_logic_vector(15 downto 0);
        weights : in std_logic_vector(15 downto 0);
        clk : in std_logic; 
        output : out std_logic
    );
end neuron_16inputs;

architecture Behavioral of neuron_16inputs is

signal products : std_logic_vector(15 downto 0);
signal threshold : integer := 4;

begin

    process(clk)
    variable weighted_sum : integer range 0 to 16 := 0;
    begin
        if rising_edge(clk) then
            weighted_sum := 0;
    
            --Products calculation
            products(0) <= inputs(0) and weights(0);
            products(1) <= inputs(1) and weights(1);
            products(2) <= inputs(2) and weights(2);
            products(3) <= inputs(3) and weights(3);
            products(4) <= inputs(4) and weights(4);
            products(5) <= inputs(5) and weights(5);
            products(6) <= inputs(6) and weights(6);
            products(7) <= inputs(7) and weights(7);  
            products(8) <= inputs(8) and weights(8);
            products(9) <= inputs(9) and weights(9);
            products(10) <= inputs(10) and weights(10);
            products(11) <= inputs(11) and weights(11);
            products(12) <= inputs(12) and weights(12);
            products(13) <= inputs(13) and weights(13);
            products(14) <= inputs(14) and weights(14);
            products(15) <= inputs(15) and weights(15);        
    
            -- Calculate weighted sum
            for i in 0 to 15 loop
                if products(i) = '1' then
                    weighted_sum := weighted_sum + 1;
                end if;
            end loop;
            
            -- Activation function
            if weighted_sum > threshold then
                output <= '1';
            else
                output <= '0';
            end if;
        end if;
    end process;
    
end Behavioral;