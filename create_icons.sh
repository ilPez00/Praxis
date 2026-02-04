#!/bin/bash

# This script creates placeholder launcher icons
# Install ImageMagick first: sudo apt install imagemagick

# Create a simple icon with ImageMagick
create_icon() {
    size=$1
    output_dir=$2
    
    convert -size ${size}x${size} xc:'#6750A4' \
            -gravity center \
            -fill white \
            -pointsize $((size/3)) \
            -annotate +0+0 'P' \
            "${output_dir}/ic_launcher.png"
    
    cp "${output_dir}/ic_launcher.png" "${output_dir}/ic_launcher_round.png"
}

# Create icons for all densities
create_icon 48 "/home/claude/Praxis/app/src/main/res/mipmap-mdpi"
create_icon 72 "/home/claude/Praxis/app/src/main/res/mipmap-hdpi"
create_icon 96 "/home/claude/Praxis/app/src/main/res/mipmap-xhdpi"
create_icon 144 "/home/claude/Praxis/app/src/main/res/mipmap-xxhdpi"
create_icon 192 "/home/claude/Praxis/app/src/main/res/mipmap-xxxhdpi"

echo "Icons created successfully!"
