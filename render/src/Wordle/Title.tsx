import {spring} from 'remotion'
import { AbsoluteFill, interpolate, useCurrentFrame, useVideoConfig } from "remotion"

export interface TitleProps {
  index: number,
  tries: number
}

export const Title: React.FC<TitleProps> = ({ index, tries }: TitleProps) => {
  const frame = useCurrentFrame();
  const config = useVideoConfig();

  const opacity = interpolate(frame, [0, 200, 250], [1, 1, 0])

  const translate = spring({frame: frame - 20, fps: config.fps, from: 0.55, to: 0, config: { stiffness: 10 }});
  const size = interpolate(frame, [10, 30], [60, 30], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });

  return (
    <AbsoluteFill style={{ opacity, textAlign: "center" }}>
      <h1 style={{
        marginTop: 30,
        fontSize: size,
        fontFamily: "Arial, sans-serif",
        fontWeight: 900,
        transform: `translateY(${translate * (config.height - 200)}px)`
      }}>
        Wordle {index} {tries}/6
      </h1>
      <h2 style={{
        position: "absolute",
        bottom: 10,
        width: "100%",
        fontFamily: "monospace",
        fontSize: 20,
        color: "#aaaaaa"
      }}>https://github.com/jirkavrba/wordle-bot</h2>
    </AbsoluteFill>
  )
}