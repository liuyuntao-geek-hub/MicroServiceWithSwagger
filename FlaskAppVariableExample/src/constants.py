import yaml

# Step 1 - Load init config
with open('resource/config.yml') as f:
    c = yaml.load(f)
threshold = c.get('threshold')