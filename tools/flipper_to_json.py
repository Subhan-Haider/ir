import json
import os
import sys
import glob

def parse_ir_file(filepath):
    """Parses a Flipper Zero .ir file and converts to intermediate JSON mapped rules"""
    try:
        with open(filepath, 'r') as f:
            lines = f.readlines()
            
        device_data = {
            "type": "unknown", 
            "brand": "unknown",
            "commands": {}
        }
        
        # Determine brand and type from filepath assumption
        # Format usually looks like: IRDB/tv/Samsung/filename.ir
        parts = filepath.replace('\\', '/').split('/')
        if len(parts) >= 3:
            device_data["brand"] = parts[-2].lower()
            device_data["type"] = parts[-3].lower()

        current_name = None
        for line in lines:
            line = line.strip()
            if line.startswith("name:"):
                current_name = line.split(":", 1)[1].strip().lower()
            elif line.startswith("data:") and current_name:
                raw_data = line.split(":", 1)[1].strip().split(" ")
                try:
                    # Filter and convert pure sequences
                    int_data = [int(x) for x in raw_data]
                    device_data["commands"][current_name] = int_data
                except ValueError:
                    pass
                current_name = None

        return device_data
    except Exception as e:
        print(f"Error parsing {filepath}: {e}")
        return None

def main():
    if len(sys.argv) < 2:
        print("Usage: python flipper_to_json.py <path_to_irdb_folder>")
        sys.exit(1)
        
    folder_path = sys.argv[1]
    search_path = os.path.join(folder_path, "**", "*.ir")
    ir_files = glob.glob(search_path, recursive=True)
    print(f"Found {len(ir_files)} .ir files.")
    
    parsed_devices = []
    for f in ir_files:
        parsed = parse_ir_file(f)
        if parsed and parsed["commands"]:
            parsed_devices.append(parsed)
            
    output_filename = "ir_database.json"
    with open(output_filename, 'w') as out_f:
        json.dump(parsed_devices, out_f, indent=2)
        
    print(f"Done. Successfully generated {output_filename} containing {len(parsed_devices)} configured devices.")

if __name__ == "__main__":
    main()
