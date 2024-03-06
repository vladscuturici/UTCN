        library IEEE;
    use IEEE.STD_LOGIC_1164.ALL;
    use ieee.std_logic_unsigned.all;
    
    entity instr_fetch is
        Port(clk : in STD_LOGIC;
            rst : in STD_LOGIC;
            BranchAddress : in STD_LOGIC_VECTOR(15 downto 0);
            JumpAddress: in STD_LOGIC_VECTOR(15 downto 0);
            Jump: in STD_LOGIC;
            PCSrc: in STD_LOGIC;
            EN: in STD_LOGIC;
            Instruction: out STD_LOGIC_VECTOR(15 downto 0);
            PCplus1: out STD_LOGIC_VECTOR(15 downto 0));
    end instr_fetch;
    
    architecture Behavioral of instr_fetch is
    
    signal EN_MPG : STD_LOGIC;
    signal pc : STD_LOGIC_VECTOR(15 downto 0):=(others => '0');
    signal m : STD_LOGIC_VECTOR(15 downto 0);
    signal mux_out : std_logic_vector(15 downto 0);
    signal A : std_logic_vector (15 downto 0);
    signal pc2 : std_logic_vector(15 downto 0);
    type ROM is array (0 to 255) of std_logic_vector (15 downto 0);
        
        signal mem: ROM := (
            B"000_000_000_001_0_001",
            B"001_100_000_0001010",
            B"000_000_000_010_0_001",
            B"000_000_000_101_0_001",
            B"000_000_000_110_0_001",
            B"100_100_001_0001110",
            B"010_010_011_0101000",
            B"001_011_011_0000011",
            B"011_010_010_0101000",
            B"110_011_111_0000001",
            B"100_000_111_0000011",
            B"000_110_011_110_0_001",
            B"111_0000000000010",
            B"000_101_011_101_0_001",
            B"001_010_010_0000100",
            B"001_001_001_0000001",
            B"111_0000000000110",
            B"011_000_101_1010000",
            B"011_000_110_1111000",
            others => x"9999"
        );
    
    begin
        PC_C: process(clk)
        begin
            if rising_edge(clk) then 
                if rst='1' then
                    pc <= (others => '0');
                elsif en = '1' then
                    pc <= a;
                end if;
            end if;
        end process;
        Instruction <= mem(conv_integer(pc(7 downto 0)));
        PC2 <= pc + 1;
        PCplus1 <= pc2;
        --mux_out <= PC2 when PCSrc ='0' else BranchAddress;
        process(PCSrc, PC2, BranchAddress)
        begin
            case PCSrc is 
                when '0' => mux_out <= pc2;
                when others => mux_out <= BranchAddress;
            end case;
        end process;
        --a <= mux_out when Jump = '0' else JumpAddress;
        process(mux_out, Jump, JumpAddress)
        begin
            case Jump is
                when '0' => a <= mux_out;
                when others => a <= JumpAddress;
            end case;
        end process;
    end Behavioral;
