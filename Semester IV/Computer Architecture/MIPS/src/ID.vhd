    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;


entity ID is
    Port ( clk : in std_logic;
           en : in std_logic;
           instr : in std_logic_vector (12 downto 0);
           wd : in std_logic_vector (15 downto 0);
           regwrite : in std_logic;
           regdst : in std_logic;
           extop : in std_logic;
           rd1 : out std_logic_vector (15 downto 0);
           rd2 : out std_logic_vector (15 downto 0);
           ext_imm : out std_logic_vector (15 downto 0);
           func : out std_logic_vector (2 downto 0);
           sa : out std_logic);       
end ID;

architecture Behavioral of ID is

signal out_mux: std_logic_vector (2 downto 0);
signal imm : std_logic_vector (6 downto 0);
type rf is array(0 to 7) of STD_LOGIC_VECTOR(15 downto 0);
signal regfile : rf := (others => X"0000");
signal wa2 : std_logic_vector (2 downto 0);

begin
    
    out_mux <= instr (9 downto 7) when regdst = '0' else instr (6 downto 4);
    process(clk)			
    begin
        if rising_edge(clk) then
            if en = '1' then 
                if regwrite = '1' then
                    regfile(conv_integer(wa2)) <= wd;		
                end if;
            end if;
        end if;
    end process;
    rd1 <= regfile(conv_integer(Instr(12 downto 10)));
    rd2 <= regfile(conv_integer(Instr(9 downto 7)));
    ext_imm(15 downto 7) <= (others => instr(6)) when extop='1' else (others => '0');
    ext_imm (6 downto 0) <= instr (6 downto 0);
    func <= instr(2 downto 0);
    sa <= instr(3);

end Behavioral;

    
