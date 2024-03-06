    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity SSD is
    Port ( digit : in STD_LOGIC_VECTOR (15 downto 0);
           clk : in STD_LOGIC;
           cat : out STD_LOGIC_VECTOR (6 downto 0);
           an : out STD_LOGIC_VECTOR (3 downto 0));
end SSD;

    architecture Behavioral of SSD is

signal tmp:STD_LOGIC_VECTOR(15 downto 0):=x"0000";
signal x:STD_LOGIC_VECTOR(3 downto 0);
begin
    process(clk)
    begin
        if rising_edge(clk) then
            tmp<=tmp+1;
        end if;
    end process;

    process(clk)
        begin
                case tmp(15 downto 14) is
                    when "00" =>  x<=digit(3 downto 0);
                    when "01" =>  x<=digit(7 downto 4);
                    when "10" =>  x<=digit(11 downto 8);
                    when  others =>  x<=digit(15 downto 12);
                end case;
    end process;

    process(clk)
    begin
        case tmp(15 downto 14) is
            when "00" =>  an<="1110";
            when "01" =>  an<="1101";
            when "10" =>  an<="1011";
            when  others => an<="0111";
        end case;
    end process;


    process(x)
    begin
        case x is            
                     when X"0" => cat <= "1000000"; --0;
                     when X"1" => cat <= "1111001"; --1
                     when X"2" => cat <= "0100100"; --2
                     when X"3" => cat <= "0110000"; --3
                     when X"4" => cat <= "0011001"; --4
                     when X"5" => cat <= "0010010"; --5
                     when X"6" => cat <= "0000010"; --6
                     when X"7" => cat <= "1111000"; --7
                     when X"8" => cat <= "0000000"; --8
                     when X"9" => cat <= "0010000"; --9
                     when X"A" => cat <= "0001000"; --A
                     when X"B" => cat <= "0000011"; --b
                     when X"C" => cat <= "1000110"; --C
                     when X"D" => cat <= "0100001"; --d
                     when X"E" => cat <= "0000110"; --E
                     when X"F" => cat <= "0001110"; --F
                     when others => cat <= "0111111"; -- gol
                end case;
    end process;
		

end Behavioral;

    
