library IEEE;
    use IEEE.STD_LOGIC_1164.ALL;
    use IEEE.NUMERIC_STD.ALL;
    
    entity reminderNeuralNetwork is
      Port ( A : in std_logic_vector (7 downto 0);
             B : in std_logic_vector (7 downto 0);
             CLK : in std_logic;
             F : out std_logic_vector (7 downto 0)
             );
    end reminderNeuralNetwork;
    
    architecture Behavioral of reminderNeuralNetwork is
    
    component neuron is
        port(
            inputs : in std_logic_vector(7 downto 0);
            weights : in std_logic_vector(7 downto 0);
            clk : in std_logic;
            output : out std_logic
        );
    end component;
    
    component neuron_16inputs is
        port(
            inputs : in std_logic_vector(15 downto 0);
            weights : in std_logic_vector(15 downto 0);
            clk : in std_logic; 
            output : out std_logic
    );  
    end component;
    
    signal input_layer_outputs : std_logic_vector(7 downto 0);
    signal hidden_layer_outputs : std_logic_vector(7 downto 0);
    signal output_layer_outputs : std_logic_vector(7 downto 0);
    
    type weight_type_16 is array(0 to 15) of std_logic_vector(15 downto 0);
    type weight_type is array(0 to 7) of std_logic_vector(7 downto 0);
    
    signal weights_input_layer: weight_type_16 := (
        "0101101001001110", 
        "0111111001001110", 
        "1001100110110000",
        "1010111110010110", 
        "1111111100111011",
        "1011111100111111",
        "0100101101001110",
        "1101111110011001",
        "0100011011111001", 
        "0111111001001110", 
        "1111111100111010",
        "1000011011001110", 
        "1101111110011011",
        "1000001101101111",
        "0111111110011011",
        "1101111111110011"
    );
    signal weights_hidden_layer: weight_type := (
        "10110010",
        "10111010",
        "10111011",
        "11010111",
        "11011011",
        "01110011",
        "01101101",
        "10011001"
    );
    signal weights_output_layer: weight_type := (
        "10100110",
        "00111101",
        "10101101",
        "00010101",
        "11001001",
        "00100110",
        "10101011",
        "11101001"
    );
    
    signal input : std_logic_vector(15 downto 0);
    begin
      
    input <= A & B;
    
    input_layer: for i in 0 to 7 generate
        input_neuron: neuron_16inputs port map(
            inputs => input, 
            weights => weights_input_layer(i), 
            clk => clk,
            output => input_layer_outputs(i) 
        );
    end generate;

    hidden_layer: for i in 0 to 7 generate
        hidden_neuron: neuron port map(
            inputs => input_layer_outputs, 
            weights => weights_hidden_layer(i), 
            clk => clk,
            output => hidden_layer_outputs(i)
        );
    end generate;

    output_layer: for i in 0 to 7 generate
        output_neuron: neuron port map(
            inputs => hidden_layer_outputs, 
            weights => weights_output_layer(i),
            clk => clk,
            output => output_layer_outputs(i) 
        );
    end generate;

    F <= output_layer_outputs;
      
    end Behavioral;
