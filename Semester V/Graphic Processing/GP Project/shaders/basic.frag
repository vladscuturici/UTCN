#version 410 core

in vec3 fPosition;
in vec3 fNormal;
in vec2 fTexCoords;

uniform int fogEnabled;
uniform bool isDirectionalLight;

out vec4 fColor;

// Matrices
uniform mat4 model;
uniform mat4 view;
uniform mat3 normalMatrix;

// Lighting
uniform vec3 lightDir;
uniform vec3 lightColor;

// Point Light Properties
uniform vec3 pointLightPos;
uniform float constant;
uniform float linear;
uniform float quadratic;

// Textures
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;

// Components
vec3 ambient;
float ambientStrength = 0.2f;
vec3 diffuse;
vec3 specular;
float specularStrength = 0.5f;



void computeDirLight() {
    // Compute eye space coordinates
    vec4 fPosEye = view * model * vec4(fPosition, 1.0f);
    vec3 normalEye = normalize(normalMatrix * fNormal);

    // Normalize light direction
    vec3 lightDirN = vec3(normalize(view * vec4(lightDir, 0.0f)));

    // Compute view direction (in eye coordinates, the viewer is at the origin)
    vec3 viewDir = normalize(-fPosEye.xyz);

    // Compute ambient light
    ambient = ambientStrength * lightColor;

    // Compute diffuse light
    diffuse = max(dot(normalEye, lightDirN), 0.0f) * lightColor;

    // Compute specular light
    vec3 reflectDir = reflect(-lightDirN, normalEye);
    float specCoeff = pow(max(dot(viewDir, reflectDir), 0.0f), 32);
    specular = specularStrength * specCoeff * lightColor;
}

void computePointLight() {
    // Compute eye space coordinates
    vec4 fPosEye = view * model * vec4(fPosition, 1.0f);
    vec3 normalEye = normalize(normalMatrix * fNormal);

    // Compute light direction
    vec3 lightDir = normalize(pointLightPos - fPosEye.xyz);

    // Compute distance to light
    float dist = length(pointLightPos - fPosEye.xyz);
    // Compute attenuation
    float att = 1.0f / (constant + linear * dist + quadratic * (dist * dist));

    // Compute ambient light
    ambient = ambientStrength * lightColor * att;

    // Compute diffuse light
    diffuse = max(dot(normalEye, lightDir), 0.0f) * lightColor * att;
    // Compute specular light
    vec3 viewDir = normalize(-fPosEye.xyz);
    vec3 reflectDir = normalize(reflect(-lightDir, normalEye));
    float specCoeff = pow(max(dot(viewDir, reflectDir), 0.0f), 32);
    specular = specularStrength * specCoeff * lightColor * att;
}

float computeFog() {
    float fogDensity = 0.02f;
    float fragmentDistance = length(view * model * vec4(fPosition, 1.0f));
    float fogFactor = exp(-pow(fragmentDistance * fogDensity, 2));

    return clamp(fogFactor, 0.0f, 1.0f);
}

void main() {
    vec3 color;

    if (isDirectionalLight) {
        computeDirLight();

        // Compute final vertex color
        color = min((ambient + diffuse) * texture(diffuseTexture, fTexCoords).rgb + specular * texture(specularTexture, fTexCoords).rgb, 1.0f);
    
    }
    else {
        computePointLight();

        // Compute final vertex color based on textures
        color = min((ambient + diffuse) * texture(diffuseTexture, fTexCoords).rgb + specular * texture(specularTexture, fTexCoords).rgb, 1.0f);
    }
    
    
    if (fogEnabled == 1) {
        float fogFactor = computeFog();
        vec4 fogColor = vec4(0.5f, 0.5f, 0.5f, 1.0f);
        fColor = mix(fogColor, vec4(color, 1.0f), fogFactor);
    } else {
        fColor = vec4(color, 1.0f); 
    }
}