library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity neuron is
    port(
        inputs : in std_logic_vector(7 downto 0);
        weights : in std_logic_vector(7 downto 0);
        clk : in std_logic; 
        output : out std_logic
    );
end neuron;

architecture Behavioral of neuron is

signal products : std_logic_vector(7 downto 0);
signal threshold : integer := 3;

begin

    process(clk)
    variable weighted_sum : integer range 0 to 8 := 0;
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
    
            -- Calculate weighted sum
            for i in 0 to 7 loop
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