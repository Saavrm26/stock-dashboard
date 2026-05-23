import React from "react";

interface ColoredIconProps {
  src: string;
  alt: string;
  className?: string;
  size?: string;
}

export const ColoredIcon: React.FC<ColoredIconProps> = ({ 
  src, 
  alt, 
  className = "", 
  size = "w-6 h-6" 
}) => {
  return (
    <img 
      src={src} 
      alt={alt} 
      className={`${size} ${className}`}
      style={{
        filter: className.includes("text-primary") 
          ? "brightness(0) invert(1)" 
          : className.includes("text-error")
          ? "brightness(0) invert(1) sepia(1) saturate(5) hue-rotate(320deg)"
          : className.includes("text-outline")
          ? "brightness(0) invert(0.5)"
          : className.includes("text-on-surface-variant")
          ? "brightness(0) invert(0.7)"
          : "none"
      }}
    />
  );
};