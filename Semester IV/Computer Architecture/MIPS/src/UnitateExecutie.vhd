    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity UnitateExecutie is
    Port(RD1 : in std_logic_vector(15 downto 0);
         AluSrc : in std_logic;
         RD2 : in std_logic_vector(15 downto 0);
         ExtImm : in std_logic_vector(15 downto 0);
         sa : in std_logic;
         func : in std_logic_vector(2 downto 0);
         AluOp : in std_logic_vector(2 downto 0);
         PcPlus1 : in std_logic_vector(15 downto 0);
         Zero : out std_logic;
         AluRes : out std_logic_vector(15 downto 0);
         BranchAdress : out std_logic_vector(15 downto 0));
end UnitateExecutie;

architecture Behavioral of UnitateExecutie is
    
signal ar : std_logic_vector(15 downto 0);
signal AluCtrl : std_logic_vector(2 downto 0);
signal op1 : std_logic_vector(15 downto 0);
signal op2 : std_logic_Vector(15 downto 0);
begin

    process(AluOp, func)
    begin
        case AluOp is
            when "000" => AluCtrl <= func;
            when "001" => AluCtrl <= "000";
            when "010" => AluCtrl <= "001";
            when others => AluCtrl <= "010"; 
        end case;  
    end process;
    
    op1 <= RD1;
    op2 <= RD2 when AluSrc = '0' else ExtImm;
    
    process(AluCtrl, op1, op2)
    begin
        case AluCtrl is
            when "001" => ar <= op1 - op2;
            when "010" => ar <= op1 - op2;
            when others => ar <= op1 + op2;
        end case;
    end process;
    
    process(AluCtrl, ar)
    begin
        
        if ar = 0 then
            Zero <= '1';
        else    
            Zero <= '0';
        end if;
    end process;
    AluRes <= ar;
    BranchAdress <= PcPlus1 + ExtImm;
end Behavioral;

    
