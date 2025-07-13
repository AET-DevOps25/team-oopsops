import React from "react";

interface LevelSliderProps {
  level: number;
  setLevel: (level: number) => void;
}

const LevelSlider: React.FC<LevelSliderProps> = ({ level, setLevel }) => {
  return (
    <div className="flex flex-col items-center">
      <input
        type="range"
        min={1}
        max={3}
        step={1}
        value={level}
        onChange={(e) => setLevel(Number(e.target.value))}
        className="w-64 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer dark:bg-gray-700"
      />
      <div className="flex justify-between w-64 text-sm text-muted-foreground mt-1">
        <span>Low</span>
        <span>Medium</span>
        <span>High</span>
      </div>
    </div>
  );
};

export default LevelSlider;
