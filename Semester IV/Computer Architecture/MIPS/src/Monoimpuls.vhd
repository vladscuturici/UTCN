    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity Monoimpuls is
    Port ( btn : in STD_LOGIC;
           clk : in STD_LOGIC;
           en : out STD_LOGIC);
end Monoimpuls;

architecture Behavioral of Monoimpuls is
Signal cnt:STD_LOGIC_VECTOR(15 downto 0):=x"0000";
Signal Q1:STD_LOGIC;
Signal Q2:STD_LOGIC;
Signal Q3:STD_LOGIC;

begin
  
  process(clk)
      begin
      if rising_edge(clk) then
        cnt<=cnt+1;
      end if;
  end process;
  
  process(clk)
  begin
      if rising_edge(clk) then
        if cnt=x"FFFF" then
           Q1<=btn;
        end if;
      end if;
  end process;
  
  process(clk)
  begin
      if rising_edge(clk) then
          Q2<=Q1;
          Q3<=Q2;
      end if;
  end process;
  
  en<=not Q3 and Q2;

end Behavioral;

    
