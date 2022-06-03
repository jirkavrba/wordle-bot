import { AbsoluteFill, interpolate, useVideoConfig } from 'remotion'
import { spring, useCurrentFrame } from "remotion";

function chunk<T>(array: Array<T>, size: number): Array<Array<T>> {
  return new Array(Math.ceil(array.length / size))
    .fill(0)
    .map((_, n) => array.slice(n * size, n * size + size));
}

interface SolutionProps {
  source: string
}

const Evaluation: React.FC<SolutionProps> = ({ source }: SolutionProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const rows = chunk([...source], 5).slice(0, 6)
  const opacity = interpolate(frame, [0, 180, 200], [1, 1, 0])

  const percentage = (r: number, c: number, linear = false): number =>
    spring({ frame: frame - (r * 30 + c * 2), fps, from: 0, to: 1, config: { stiffness: linear ? 10 : 100 } });

  const color = (letter: string) => ({
    "A": "#86888a",
    "P": "#c9b458",
    "C": "#6aaa64",
  })[letter];

  return <AbsoluteFill style={{ opacity, display: "flex", flexFlow: "column nowrap", justifyContent: "center", alignItems: "center" }}>
    {rows.map((row, r) =>
      <div style={{ display: "flex", flexFlow: "row nowrap", justifyContent: "center" }}>
        {row.map((cell, c) =>
          <div style={{
            width: 60,
            height: 60,
            margin: 10,
            transform: `translateY(${30 - percentage(r, c) * 30}px)`,
            borderRadius: 5,
            backgroundColor: color(cell),
            opacity: percentage(r, c, true)
          }} />
        )}
      </div>
    )}
  </AbsoluteFill>
}

export default Evaluation;