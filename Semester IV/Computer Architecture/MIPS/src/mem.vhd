    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all;

entity mem is
    Port (MemWrite : IN std_logic;
          AluRes : IN std_logic_vector(15 downto 0);
          RD2 : IN std_logic_vector(15 downto 0);
          CLK : IN std_logic;
          MPG_EN : IN std_logic;
          AluRes2 : OUT std_logic_vector(15 downto 0);
          MemData : OUT std_logic_vector(15 downto 0)
          );
end mem;

architecture Behavioral of mem is

    type mem is array (0 to 31) of STD_LOGIC_VECTOR(15 downto 0);
    signal ram : mem := (
        X"000A",
        X"000B",
        X"000C",
        X"000D",
        X"000E",
        X"000F",
        X"0009",
        X"0008",
        others => X"0000");

begin

    process(clk) 			
    begin
        if rising_edge(clk) then
            if MPG_EN = '1' then
                if MemWrite='1' then
                    ram(conv_integer(ALURes(4 downto 0))) <= rd2;			
                end if;
            end if;
        end if;
    end process;
    MemData <= ram(conv_integer(ALURes(4 downto 0)));

end Behavioral;

    
