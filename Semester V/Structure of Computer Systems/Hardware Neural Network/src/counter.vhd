library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL; 

entity counter is
    Port ( N : in std_logic_vector(7 downto 0);
           CLK : in std_logic;
           EN : in std_logic;
           Reset : in std_logic;
           CNT : out std_logic_vector(7 downto 0)     
          );
end counter;

architecture Behavioral of counter is
    signal count: std_logic_vector(7 downto 0) := "00000010";
begin
    counter: process(CLK)
    begin
        if rising_edge(CLK) then
            if Reset = '1' then
                count <= "00000010";
            elsif EN = '1' then
                count <= std_logic_vector(unsigned(count) + 1);
            end if;
        end if;
    end process;

    CNT <= count;
end Behavioral;
